name 'myapp'
maintainer 'Markus'
maintainer_email 'Max.Mustermann@muster.de'
license 'Apache-2.0'
description 'Installs and configures MyApp'
version '1.0.0'


%w(ubuntu centos fedora smartos suse debian).each do |os|
  supports os
end

depends 'java', '<= 4.0.0'

source_url 'https://github.com/chef-cookbooks/openssh'
issues_url 'https://github.com/chef-cookbooks/openssh/issues'
chef_version '>= 12.1' if respond_to?(:chef_version)
