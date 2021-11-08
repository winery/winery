Winery CLI
##########

The Winery CLI can be used to perform a consistency check for a given repository.

- Linux: ``docker run -it -v $(pwd):/root/winery-repository opentosca/winery-cli winery -v``
- Windows: ``docker run -it -v ${PWD}:/root/winery-repository opentosca/winery-cli winery -v``

.. note::
   You may replace ``$(pwd)`` or ``${PWD}`` with a directory location on your Docker host system.

Currently supported CLI arguments:

.. code-block::

    -h,--help         prints this help
    -p,--path <arg>   use given path as repository path
    -v,--verbose      be verbose: output the checked elements
