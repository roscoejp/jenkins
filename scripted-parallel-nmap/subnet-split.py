#!/usr/bin/env python
# -*- coding: utf-8 -*-
from netaddr import IPNetwork
from os import environ
import argparse

parser = argparse.ArgumentParser()
parser.add_argument('-c', '--cidr' , help='Subnet CIDR that should be split.', default='127.0.0.1')
parser.add_argument('-n', '--nodes', help='Number to workers we have available.', type=int, default=1)
parser.add_argument('-d', '--debug', action='store_true', help='Debug')
args = parser.parse_args()

subnet = IPNetwork(args.cidr)
nodes = args.nodes - 1          # -1 accounts for the current worker

if args.debug:
    subnet = IPNetwork('8.0.0.8/8')
    nodes = 2 

new_prefix = subnet.prefixlen + nodes if (subnet.prefixlen < (32 - nodes)) else 32
subnets = [str(split) for split in subnet.subnet(new_prefix)]
print ', '.join(subnets)
