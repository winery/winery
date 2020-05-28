#  Copyright (c) 2020 Contributors to the Eclipse Foundation
# 
#  See the NOTICE file(s) distributed with this work for additional
#  information regarding copyright ownership.
# 
#  This program and the accompanying materials are made available under the
#  terms of the Eclipse Public License 2.0 which is available at
#  http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
#  which is available at https://www.apache.org/licenses/LICENSE-2.0.
# 
#  SPDX-License-Identifier: EPL-2.0 OR Apache-2.0

# Configuration file for the Sphinx documentation builder.
#
# This file only contains a selection of the most common options. For a full
# list see the documentation:
# https://www.sphinx-doc.org/en/master/usage/configuration.html

# -- Path setup --------------------------------------------------------------

# If extensions (or modules to document with autodoc) are in another directory,
# add these directories to sys.path here. If the directory is relative to the
# documentation root, use os.path.abspath to make it absolute, like shown here.
#
# import os
import datetime
from recommonmark.parser import CommonMarkParser
from recommonmark.transform import AutoStructify

now = datetime.datetime.now()

# -- Project information -----------------------------------------------------

project = 'Eclipse Winery'
copyright = '(c) 2013-{} Contributors to the Eclipse Foundation.'.format(now.year)
author = 'Contributors to the Eclipse Foundation'

# -- General configuration ---------------------------------------------------

# Add any Sphinx extension module names here, as strings. They can be
# extensions coming with Sphinx (named 'sphinx.ext.*') or your custom
# ones.
extensions = [
    'sphinx_markdown_tables',
    'recommonmark',
    'sphinx.ext.autosectionlabel',
]

# Add any paths that contain templates here, relative to this directory.
templates_path = []

# List of patterns, relative to source directory, that match files and
# directories to ignore when looking for source files.
# This pattern also affects html_static_path and html_extra_path.
exclude_patterns = ['_build', 'Thumbs.db', '.DS_Store', '_legacy', 'README.md', 'index.html']

source_parsers = {
    '.md': CommonMarkParser,
}

source_suffix = ['.rst', '.md']

# -- Options for HTML output -------------------------------------------------

# The theme to use for HTML and HTML Help pages.  See the documentation for
# a list of builtin themes.
html_theme = 'sphinx_rtd_theme'

# Add any paths that contain custom static files (such as style sheets) here,
# relative to this directory. They are copied after the builtin static files,
# so a file named "default.css" will overwrite the builtin "default.css".
html_static_path = ['_static']


def setup(app):
    app.add_config_value('recommonmark_config', {
        'auto_toc_tree': False,
        'enable_eval_rst': True,
        'enable_math': True,
        'enable_inline_math': True,
    }, True)
    app.add_transform(AutoStructify)
