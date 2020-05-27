.. Copyright (c) 2020 Contributors to the Eclipse Foundation

.. See the NOTICE file(s) distributed with this work for additional
.. information regarding copyright ownership.

.. This program and the accompanying materials are made available under the
.. terms of the Eclipse Public License 2.0 which is available at
.. http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
.. which is available at https://www.apache.org/licenses/LICENSE-2.0.

.. SPDX-License-Identifier: EPL-2.0 OR Apache-2.0

.. _getting_started:


Getting Started
###############

Launching with Docker
*********************

.. note::
   It is recommended that your host or virtual machine has at least 2GB of memory.

Open a command prompt and execute the following command:

.. code-block::

   docker run -it -p 8080:8080 \
     -e PUBLIC_HOSTNAME=localhost \
     -e WINERY_FEATURE_RADON=true \
     -e WINERY_REPOSITORY_PROVIDER=yaml \
     -e WINERY_REPOSITORY_URL=https://github.com/radon-h2020/radon-particles \
     opentosca/radon-gmt

Launch a browser: `<http://localhost:8080>`_.

.. note::
   To start Eclipse Winery based on an TOSCA XML repository layout, use the following command:

   .. code-block::

      docker run -it -p 8080:8080 \
        -e PUBLIC_HOSTNAME=localhost \
        -e WINERY_REPOSITORY_URL=https://github.com/OpenTOSCA/tosca-definitions-public \
        opentosca/winery

.. note:: 
   Make sure you regularly pull the latest images:

   .. code-block::

      docker pull opentosca/radon-gmt:latest
      # or
      docker pull opentosca/winery:latest


Use a custom TOSCA model repository
-----------------------------------

Please follow the next instructions to mount an existing TOSCA model repository into the Eclipse Winery container.
This is useful if you want to save your modeling changes onto your Docker host machine. 

Clone or create git repository on your local filesystem, e.g., by cloning `<https://github.com/radon-h2020/radon-particles>`_.

Open a command prompt and execute the following command:

.. warning::
   Replace ``<path_on_your_host>`` with the respective dirctory path on your host system.

.. code-block::

   docker run -it -p 8080:8080 \
     -e PUBLIC_HOSTNAME=localhost \
     -e WINERY_FEATURE_RADON=true \
     -e WINERY_REPOSITORY_PROVIDER=yaml \
     -v <path_on_your_host>:/var/repository \
     -u `id -u` \
     opentosca/radon-gmt

Launch a browser: `<http://localhost:8080>`_.

Any change (create service template, modify or create node types) will be reflected on your host machine.
You are now able to commit your changes and push them to your own Git remote (e.g., using ``git push`` from a command-prompt).

.. note::
   To start Eclipse Winery based on an TOSCA XML repository layout, use the following command:

   .. code-block::

      docker run -it -p 8080:8080 \
        -e PUBLIC_HOSTNAME=localhost \
        -v <path_on_your_host>:/var/repository \
        opentosca/winery


Launching with Docker Compose
*****************************

.. note::
   It is recommended that your host or virtual machine has at least 2GB of memory.

Install Docker and `Docker Compose <https://docs.docker.com/compose>`_.

Clone the repository:

.. code-block::

   git clone https://github.com/eclipse/winery
   cd winery/deploy/compose

**[Optional]** Build the Docker image based on your current working copy:
  
.. code-block::

   docker-compose build

**[Optional]** Adapt the Docker Compose configuration to your needs, e.g., to mount a local TOSCA model repository.

Start Winery:

.. code-block::

   docker-compose up

Launch a browser: `<http://localhost:8080>`_.
