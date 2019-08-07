# This is just a demo recipe

include_recipe 'java::default'

package 'package1' do
    package_name 'myapp'
    version '1.1'
    action :install
end


package node['myapp']['package'] do
    version node['myapp']['version']
    action :install
end
