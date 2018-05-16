#!/usr/bin/env python
import logging
import tarfile
import argparse
import sys, json
from time import strftime
from os import path, remove, makedirs
from splunklib.client import connect, HTTPError

def main():

    parser = argparse.ArgumentParser(description='NMap based dynamic inventory')
    
    parser.add_argument('-d', '--debug', action='store_true', help='Debug')
    parser.add_argument('--tar', action='store_true', help='Create tar archive of output files.')
    parser.add_argument('--username', help='Splunk management username.')
    parser.add_argument('--password', help='Splunk management password.')
    parser.add_argument('--host', help='Splunk KVStore FQDN.')
    parser.add_argument('--port', help='Splunk KVStore management port.', default="8089")    
    parser.add_argument('--app', help='Application to backup.')
    parser.add_argument('--owner', help='Namespace for collection. Should be "nobody"', default="nobody")
    
    args = parser.parse_args()

    artifact_dir = path.join(path.dirname(path.realpath(__file__)), 'artifacts')
    # Make sure artifact dir is there since we use it
    if not path.exists(artifact_dir):
        try:
            makedirs(artifact_dir)
        except: # Guard against race condition
            pass
    file_names = []

    with open(path.join(artifact_dir, strftime("%m%d%Y") + '.log'), 'w+') as log_file: 
        logging.basicConfig(filename=log_file.name,level=logging.INFO, format='[%(asctime)s] %(levelname)-8s %(message)s', datefmt='%m/%d/%Y %H:%M:%S')

    # Create connect kwargs from arguments
    kwargs_list = ['username', 'password', 'host', 'port', 'owner', 'app']
    kwargs = {key: value for key, value in vars(args).items() if key in kwargs_list}
    service = connect(**kwargs)

    # Backup all of application keystore since we can't do only lookups
    for collection in service.kvstore:
        data_file_name = path.join(artifact_dir, collection.name + '.json')
        try:
            with open(data_file_name, 'w+') as out_file:
                json.dump(collection.data.query(), out_file, indent=1)
                logging.info("Exported data for: %s to %s" % (collection.name, data_file_name))
                file_names.append(data_file_name)
        except HTTPError as e:
            logging.warning("Error exporting: %s" % collection.name + " : " + str(e))
            remove(data_file_name)

    # Create tar file
    if args.tar:
        with tarfile.open(path.join(artifact_dir, 'backup.tgz'), 'w:gz') as tar_file:
            for name in file_names:
                try:
                    tar_file.add(name, arcname=path.basename(name))
                    logging.info("Added %s to tar as %s" % (name, path.basename(name)))
                except Exception as e:
                    logging.warning("Error tarring file: %s : %s" % (name, e))
            # Validate tar content before cleaning up
            validator_names = [path.basename(i) for i in file_names]
            if bool(set(tar_file.getnames()).intersection(validator_names)):
                logging.info("Cleaning up archived files.")
                for name in file_names:
                    remove(name)
            else:
                logging.warning("Tar does not contain full output. Maintaining individual files.")

if __name__ == "__main__":
    main()
