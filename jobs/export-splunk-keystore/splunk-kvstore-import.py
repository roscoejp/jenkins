#!/usr/bin/env python
import logging
import tarfile
import argparse
import sys, json
from time import strftime
from os import path
from splunklib.client import connect, HTTPError

def main():

    parser = argparse.ArgumentParser(description='NMap based dynamic inventory')
    
    parser.add_argument('-d', '--debug', action='store_true', help='Debug')
    parser.add_argument('--username', help='Splunk management username.')
    parser.add_argument('--password', help='Splunk management password.')
    parser.add_argument('--host', help='Splunk KVStore FQDN.')
    parser.add_argument('--port', default="8089", help='Splunk KVStore management port.', )    
    parser.add_argument('--app', help='Application to backup.')
    parser.add_argument('--owner', help='Namespace for collection. Should be "nobody"', default="nobody")
    parser.add_argument('--tar', help='Tar of files to restore from.')
    
    args = parser.parse_args()

    artifact_dir = path.join(path.dirname(path.realpath(__file__)), 'artifacts')

    with open(path.join(artifact_dir, strftime("%m%d%Y") + '.log'), 'w+') as log_file: 
        logging.basicConfig(filename=log_file.name,level=logging.INFO, format='[%(asctime)s] %(levelname)-8s %(message)s', datefmt='%m/%d/%Y %H:%M:%S')

    # Debug options
    if args.debug:
        args = parser.parse_args([
        '--username=$USERNAME',
        '--password=$PASSWORD',
        '--host=$SPLUNK_DB_HOST',
        '--app=$APP_NAME',
        '--owner=nobody', 
        '--debug',
        '--tar=' + path.join(artifact_dir, 'backup.tgz')])
        logging.info("Running with debug parameters: %s" % args)

    # Create connect kwargs from arguments
    kwargs_list = ['username', 'password', 'host', 'port', 'owner', 'app']
    kwargs = {key: value for key, value in vars(args).items() if key in kwargs_list}
    service = connect(**kwargs)

    try:
        with tarfile.open(path.join(artifact_dir, args.tar), 'r:gz') as tar_file:
            for member in tar_file.getmembers():
                name = path.splitext(path.basename(member.name))[0]
                if name == 'conf_spaces_lookup':
                    # Do the actual restore here     
                    collection = service.kvstore[name]
                    logging.info("Restoring lookup: %s" % name)
                    data = json.load(tar_file.extractfile(member))
                    collection.data.batch_save(*data)
    except HTTPError as e:
        logging.error("Error restoring group: %s : %s" % (name, str(e)))
    except Exception as e:
        logging.error("Error reading tarfile: %s" % str(e))   

if __name__ == "__main__":
    main()
