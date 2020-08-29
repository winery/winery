/*******************************************************************************
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 *******************************************************************************/

package org.eclipse.winery.model.tosca.dependencyresolver;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import javax.xml.bind.JAXBException;

import org.eclipse.winery.model.tosca.dependencyresolver.Abstract.Language;
import org.eclipse.winery.model.tosca.dependencyresolver.xml_definitions.RR_DependsOn;
import org.eclipse.winery.model.tosca.dependencyresolver.xml_definitions.RR_PreDependsOn;

// import tosca.xml_definitions.PackageTemplate;

public class Package_Handler {

    static public final String Extension = ".deb";
    static public final String ScriptExtension = ".sh";

    // list with renamed packages
    private final HashMap<String, String> rename;

    // list with already downloaded packages
    private final List<String> downloaded;

    // packets to be ignored
    private final List<String> ignore;

    private final CSAR_handler ch;

    /**
     * Constructor
     */
    public Package_Handler(final CSAR_handler new_ch) {
        this.downloaded = new LinkedList<>();
        this.ignore = new LinkedList<>();
        this.rename = new HashMap<>();
        this.ch = new_ch;
    }

    /**
     * Downloads packet, public functions. Calls private recursive function
     *
     * @param language
     * @param packet to be download
     * @param source
     * @throws JAXBException
     * @throws IOException
     */
    public List<String> getPacket(final Language language, final String packet,
                                  final String source) throws JAXBException, IOException {
        final List<String> listed = new LinkedList<>();

        if (this.ch.getResolving() == CSAR_handler.Resolving.Archive) {
            downloadAllDependencies(packet);
        } else {
            getPacket(language, packet, listed, source, source);
        }
        return listed;
    }

    /**
     * @param language
     * @param packet
     * @param listed
     * @param source
     * @param sourcefile
     * @throws JAXBException
     * @throws IOException
     */
    public void getPacket(final Language language, String packet, final List<String> listed, final String source,
                          final String sourcefile) throws JAXBException, IOException {
        if (this.rename.containsKey(packet)) {
            packet = this.rename.get(packet);
        }
        System.out.println("Get packet: " + packet);
        // if(packet.equals("initscripts:i386"))
        // System.out.println("alilua");
        // if package is already listed: nothing to do
        if (listed.contains(packet) || this.ignore.contains(packet)) {
            return;
        }
        // if this is the first call of recursive function, we need to add
        // architecture to package
        // but some packages are multyarchitecture, need to check it.
        if (source.equals(sourcefile)) {
            if (packetExists(packet + this.ch.getArchitecture()) && !isVirtual(packet + this.ch.getArchitecture())) {
                packet = packet + this.ch.getArchitecture();
            }
        }
        while (!packetExists(packet)) {
            packet = getSolution(packet);
            if (packet.equals("")) {
                return;
            }
        }
        String newName;
        List<String> dependensis;
        dependensis = getDependensies(packet);
        // check if package was already downloaded
        if (listed.contains(packet)) {
            return;
        }
        listed.add(packet);
        if (!this.downloaded.contains(packet)) {
            this.downloaded.add(packet);
            packet = downloadPackage(packet);
            if (packet.equals("")) {
                return;
            }
        }
        listed.add(packet);
        newName = Utils.correctName(packet);
        if (this.ch.getResolving() == CSAR_handler.Resolving.Mirror) {
            final String nodename = language.createTOSCA_Node(newName, sourcefile);
            if (source.equals(sourcefile)) {
                this.ch.AddDependenciesScript(Utils.correctName(source), nodename);
            } else {
                this.ch.AddDependenciesPacket(language.getNodeName(source, sourcefile), nodename,
                                              getDependencyType(source, packet));
            }
        }
        for (final String dPacket : dependensis) {

            getPacket(language, dPacket, listed, packet, sourcefile);
        }

        return;
    }

    public void downloadAllDependencies(final String packet) {
        String dir_name = "";
        String newName;
        Process proc;

        final Runtime rt = Runtime.getRuntime();

        final String cmd =
            "apt-get download $(apt-cache depends --recurse --no-recommends --no-suggests --no-conflicts --no-breaks --no-replaces --no-enhances --no-pre-depends "
                + packet + " | grep \"^\\w\")";

        System.out.println("Command: " + cmd);
        try {
            proc = rt.exec(new String[] {"bash", "-c", cmd});
            final BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

            final BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

            String s = null;
            while ((s = stdInput.readLine()) != null) {
                System.out.print(s);
            }

            // read any errors from the attempted command
            System.out.println("Errors:\n");
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }
            proc.waitFor();
            System.out.println("done");

            // need to move package to right folder
            for (final File entry : new File("./").listFiles()) {

                if (!zip.getFileExtension(entry).equals("CSAR") && !zip.getFileExtension(entry).equals("csar")
                    && !zip.getFileExtension(entry).equals("jar")) {

                    // System.out.println("downloaded and found: " + entry.getName());
                    newName = Utils.correctName(entry.getName().replace(",deb", ""));
                    dir_name = Resolver.folder + newName + File.separator;
                    new File(this.ch.getFolder() + dir_name).mkdirs();
                    entry.renameTo(new File(this.ch.getFolder() + dir_name + newName + Extension));
                }
            }

        }
        catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (final InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private String downloadPackage(String packet) {
        final String sourceName = packet;
        Boolean downloaded = false;
        String dir_name = "";
        String newName;
        Process proc;
        while (!downloaded) {
            try {
                if (this.ch.debug) {
                    System.out.println("debug imitation: " + packet);
                    Utils.createFile(this.ch.getFolder() + Resolver.folder + Utils.correctName(packet) + File.separator
                        + Utils.correctName(packet) + Extension, "");
                    break;
                }
                // "apt-get download" downloads only to current folder
                System.out.println("apt-get download " + packet);
                final Runtime rt = Runtime.getRuntime();
                proc = rt.exec("apt-get download " + packet);

                final BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

                final BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

                String s = null;
                while ((s = stdInput.readLine()) != null) {
                    System.out.print(s);
                }

                // read any errors from the attempted command
                System.out.println("Errors:\n");
                while ((s = stdError.readLine()) != null) {
                    System.out.println(s);
                }
                proc.waitFor();
                System.out.println("done");
                // need to move package to right folder
                for (final File entry : new File("./").listFiles()) {
                    if (file_downloaded(packet, entry)) {
                        // System.out.println("downloaded and found: " + entry.getName());
                        newName = Utils.correctName(packet);
                        dir_name = Resolver.folder + newName + File.separator;
                        new File(this.ch.getFolder() + dir_name).mkdirs();
                        entry.renameTo(new File(this.ch.getFolder() + dir_name + newName + Extension));
                        downloaded = true;
                        if (sourceName != packet) {
                            this.rename.put(sourceName, packet);
                        }
                        break;
                    }
                }
                if (downloaded == false && !packet.contains(this.ch.getArchitecture())) {
                    packet = packet + this.ch.getArchitecture();
                } else if (downloaded == false) {
                    System.out.println("downloaded packet " + packet + " not found");

                    packet = getSolution(packet);
                    if (packet.equals("")) {
                        return "";
                    }
                }

            }
            catch (final IOException e) {
                System.out.println("Download" + packet + "failed");
                e.printStackTrace();
            }
            catch (final InterruptedException e) {
                System.out.println("Download" + packet + "failed");
                e.printStackTrace();
            }
        }
        return packet;
    }

    private Boolean file_downloaded(final String packet, final File file) {
        return (file.getName().endsWith(this.ch.getArchitecture().replaceAll(":", "") + Extension)
            || file.getName().endsWith("all" + Extension))
            && (packet.contains(":") && file.getName().startsWith(packet.substring(0, packet.indexOf(':')))
                || !packet.contains(":") && file.getName().startsWith(packet));
    }

    /**
     * Get dependency list for package
     *
     * @param packet , package to be checked
     * @return list with depended packages
     * @throws IOException
     */
    private List<String> getDependensies(String packet) throws IOException {
        if (this.rename.containsKey(packet)) {
            packet = this.rename.get(packet);
        }
        final List<String> depend = new LinkedList<>();
        final Runtime rt = Runtime.getRuntime();
        final Process proc = rt.exec("apt-cache depends " + packet);

        final BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

        System.out.print("temp dependensis : ");
        String s = null;
        // TODO Predepends
        while ((s = stdInput.readLine()) != null) {
            addDependensyString(s, stdInput, depend);
        }
        System.out.println("");
        if (this.ch.debug) {
            while (depend.size() > 1) {
                depend.remove(0);
            }
        }
        System.out.print("final dependensis : ");
        for (final String dependency : depend) {
            System.out.print(dependency + ",");
        }
        System.out.print("\n");
        return depend;
    }

    private void addDependensyString(final String s, final BufferedReader stdInput,
                                     final List<String> depend) throws IOException {
        if (s == null) {
            return;
        }
        final String[] words = s.replaceAll("[;&]", "").split("\\s+");
        if (words.length == 3 && (words[1].equals("Depends:") || words[1].equals("PreDepends:"))) {
            final List<String> currentDepend = new LinkedList<>();
            if (words[2].startsWith("<") && words[2].endsWith(">")) {
            } else {
                currentDepend.add(words[2]);
                System.out.print(words[2] + ",");
            }
            addDependensySubString(stdInput.readLine(), stdInput, depend, currentDepend);
        }
    }

    private void addDependensySubString(final String s, final BufferedReader stdInput, final List<String> depend,
                                        final List<String> currentDepend) throws IOException {
        if (s == null) {
            processCurrentDepend(depend, currentDepend);
            return;
        }
        final String[] words = s.replaceAll("[;&]", "").split("\\s+");
        if (words.length == 3 && (words[1].equals("Depends:") || words[1].equals("PreDepends:"))) {
            processCurrentDepend(depend, currentDepend);
            if (words[2].startsWith("<") && words[2].endsWith(">")) {
            } else {
                currentDepend.add(words[2]);
                System.out.print(words[2] + ",");
            }
            addDependensySubString(stdInput.readLine(), stdInput, depend, currentDepend);
        } else if (words.length == 2) {
            if (words[1].startsWith("<") && words[1].endsWith(">")) {
            } else {
                currentDepend.add(words[1]);
                System.out.print(words[1] + ",");
            }
            addDependensySubString(stdInput.readLine(), stdInput, depend, currentDepend);
        }
    }

    private void processCurrentDepend(final List<String> depend, final List<String> currentDepend) {
        if (currentDepend == null || currentDepend.size() == 0) {
            return;
        }

        if (currentDepend.size() == 1) {
            depend.addAll(currentDepend);
            currentDepend.clear();
            return;
        }

        for (final String dependency : currentDepend) {
            if (dependency.endsWith(this.ch.getArchitecture())) {
                depend.add(dependency);
                currentDepend.clear();
                return;
            }
        }

        for (final String dependency : currentDepend) {
            if (!dependency.contains(":")) {
                depend.add(dependency);
                currentDepend.clear();
                return;
            }
        }
    }

    /**
     * Checks if packet can be download
     *
     * @param packet to check
     * @return
     * @throws IOException
     */
    private boolean packetExists(String packet) throws IOException {

        if (this.rename.containsKey(packet)) {
            packet = this.rename.get(packet);
        }
        final Runtime rt = Runtime.getRuntime();
        final Process proc = rt.exec("apt-cache depends " + packet);
        final BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

        if (stdError.readLine() != null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Ask user for solution, by undownloadable package
     *
     * @param packet old package name
     * @returnnew package name
     */
    @SuppressWarnings("resource")
    private String getSolution(final String packet) {
        while (true) {
            System.out.println("cant find packet: " + packet);
            System.out.println("1) rename");
            System.out.println("2) retry");
            System.out.println("3) ignore");
            if (packet.contains(":")) {
                System.out.println("4) remove architecture");
            }
            try {
                final int action = new Scanner(System.in).nextInt();
                switch (action) {
                    case 1:
                        System.out.print("Enter new name: ");
                        final String temp = new Scanner(System.in).nextLine();
                        if (temp != null && !temp.equals("")) {
                            return temp;
                        } else {
                            System.out.println("incorect name");
                        }
                        break;
                    case 3:
                        this.ignore.add(packet);
                        System.out.println("packet " + packet + " added to ignore list");
                        return "";
                    case 4:
                        return packet.substring(0, packet.indexOf(':'));
                }
                return packet;
            }
            catch (final InputMismatchException e) {

            }

        }
    }

    public String getDependencyType(final String source, final String target) throws IOException {
        final Runtime rt = Runtime.getRuntime();
        final Process proc = rt.exec("apt-cache depends " + source);

        final BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

        String s = null;
        String last = null;
        while ((s = stdInput.readLine()) != null) {
            final String[] words = s.replaceAll("[;&<>]", "").split("\\s+");
            if (words.length == 3 && words[1].equals("Depends:") && words[2].equals(target)) {
                return RR_DependsOn.Name;
            }

            if (words.length == 3 && words[1].equals("PreDepends:") && words[2].equals(target)) {
                return RR_PreDependsOn.Name;
            }
            if (words.length == 2 && words[1].equals(target)) {
                return last;
            }
            if (words.length > 1 && words[1].equals("Depends:")) {
                last = RR_DependsOn.Name;
            }
            if (words.length > 1 && words[1].equals("PreDepends:")) {
                last = RR_PreDependsOn.Name;
            }
        }
        if (this.rename.containsKey(source) && source != this.rename.get(source)) {
            return getDependencyType(this.rename.get(source), target);
        }
        if (this.rename.containsKey(target) && target != this.rename.get(target)) {
            return getDependencyType(source, this.rename.get(target));
        }
        return null;
    }

    private boolean isVirtual(String packet) throws IOException {
        if (this.rename.containsKey(packet)) {
            packet = this.rename.get(packet);
        }
        final Runtime rt = Runtime.getRuntime();
        final Process proc = rt.exec("apt-cache show " + packet);

        final BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

        System.out.print("dependensis : ");
        final String s = stdInput.readLine();
        if (s == null || s.startsWith("N:")) {
            return true;
        }
        return false;
    }
}
