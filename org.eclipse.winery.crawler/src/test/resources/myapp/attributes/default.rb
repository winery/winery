default['myapp']['package_name'] = case node['platform_family']
                                     when 'rhel', 'fedora', 'amazon'
                                       'myapp_platform1'
                                     when 'arch', 'suse', 'gentoo'
                                       'myapp_platform2'
                                     when 'freebsd', 'smartos', 'mac_os_x', 'aix'
                                       'myapp_platform3'
                                     else
                                       %w(myapp_platform4 myapp_secondPackage)
                                     end


default['myapp']['package_name2'] = "package1"

default['myapp']['package'] = 'myappaddon'

default['myapp']['version'] = '1.2'
