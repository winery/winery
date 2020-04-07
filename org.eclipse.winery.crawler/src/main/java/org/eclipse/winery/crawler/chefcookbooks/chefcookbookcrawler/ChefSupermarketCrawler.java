/*******************************************************************************
 * Copyright (c) 2019 Contributors to the Eclipse Foundation
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

package org.eclipse.winery.crawler.chefcookbooks.chefcookbookcrawler;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.winery.crawler.chefcookbooks.helper.Version;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.IOUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class consists exclusively of static methods that operate on crawling chef cookbook artefacts from the Chef
 * Supermarket.
 */
public class ChefSupermarketCrawler {

    private static final int BUFFER_SIZE = 4096;

    private static final Logger LOGGER = LoggerFactory.getLogger(ChefSupermarketCrawler.class.getName());

    private String tempDirectory;

    private String cookbookDirectory;

    public ChefSupermarketCrawler(String cookbookDirectory, String tempDirectory) {
        this.cookbookDirectory = cookbookDirectory;
        this.tempDirectory = tempDirectory;

        initCrawler(cookbookDirectory, tempDirectory);
    }

    /**
     * Crawl all available Cookbooks from Supermarket. Crawling process of cookbooks is done in multiple threads.
     */
    public void getAvailableCookbooksFast() throws Exception {
        int startOffset = 0;
        int itemsPerLoop = 0;
        int totalNumCookbooks;
        List<CrawlCookbookRunnable> crawlCookbookRunnableList = new ArrayList<>();

        String supermarketApiRequest = getSupermarketApiRequest(startOffset, itemsPerLoop);
        JSONObject totalCookbooks = getJsonFromUrl(supermarketApiRequest);
        totalNumCookbooks = totalCookbooks.getInt("total");

        itemsPerLoop = 50; //can be a number between one 1 and 1000
        while (startOffset < totalNumCookbooks) {
            String supermarketApiRequest2 = getSupermarketApiRequest(startOffset, itemsPerLoop);
            JSONObject cookbooksToCrawl = getJsonFromUrl(supermarketApiRequest2); // Maximum is 1000 items in response

            JSONArray cookbooksToCrawlArray = cookbooksToCrawl.getJSONArray("items");

            for (int cbIndex = 0; cbIndex < cookbooksToCrawlArray.length(); cbIndex++) {
                //Get a cookbook from array
                JSONObject cookbookItem = cookbooksToCrawlArray.getJSONObject(cbIndex);
                // Get name of cookbook
                String cookbookName = cookbookItem.getString("cookbook_name");

                CrawlCookbookRunnable crawlCookbook = new CrawlCookbookRunnable(cookbookName, this.cookbookDirectory, this.tempDirectory);
                crawlCookbook.start();
                crawlCookbookRunnableList.add(crawlCookbook);
            }
            // Wait for threads
            for (int i = 0; i < crawlCookbookRunnableList.size(); i++) {
                try {
                    crawlCookbookRunnableList.get(i).join();
                } catch (InterruptedException e) {
                    LOGGER.info("Crawling process of cookbook" + crawlCookbookRunnableList.get(i).getThreadName() + " interrupted");
                }
            }
            crawlCookbookRunnableList.clear();
            //Adjust Offset
            startOffset = startOffset + itemsPerLoop;
        }
    }

    /**
     * Crawl all available Cookbooks from Chef Supermarket. Cookbooks are downloaded one after the other. Deprecated
     * cookbooks are skipped.
     */
    public void getAvailableCookbooks() throws Exception {
        int startOffset = 0;
        int itemsPerLoop = 0;
        int totalNumCookbooks;

        String supermarketApiRequest = getSupermarketApiRequest(startOffset, itemsPerLoop);
        JSONObject totalCookbooks = getJsonFromUrl(supermarketApiRequest);
        totalNumCookbooks = totalCookbooks.getInt("total");

        itemsPerLoop = 50; //can be a number between 1 and 1000
        while (startOffset < totalNumCookbooks) {
            String supermarketApiRequestIteration = getSupermarketApiRequest(startOffset, itemsPerLoop);
            JSONObject cookbooksToCrawl = getJsonFromUrl(supermarketApiRequestIteration); // Maximum is 1000 items in response

            JSONArray cookbooksToCrawlArray = cookbooksToCrawl.getJSONArray("items");

            for (int cbIndex = 0; cbIndex < cookbooksToCrawlArray.length(); cbIndex++) {
                //Get a cookbook from array
                JSONObject cookbookItem = cookbooksToCrawlArray.getJSONObject(cbIndex);
                // Get url of cookbook for further information
                String cookbookUrl = cookbookItem.getString("cookbook");
                //Get JSON Data for a specific cookbook
                JSONObject cookbookJSONObject = getJsonFromUrl(cookbookUrl);

                processCookbook(cookbookJSONObject);
            }
            //Adjust Offset
            startOffset = startOffset + itemsPerLoop;
        }
    }

    /**
     * Crawl a specific cookbook from Chef Supermarket by cookbook name.
     *
     * @param cookbookName This is the name of the requested cookbook.
     * @return True when cookbook is crawled. False when not crawled because cookbook is deprecated.
     * @throws Exception On crawling error.
     */
    public boolean getCookbook(String cookbookName) throws Exception {
        boolean cookbookDeprecated;

        String supermarketApiRequest = getSupermarketApiRequest(cookbookName);
        JSONObject specificCookbook = getJsonFromUrl(supermarketApiRequest);

        cookbookDeprecated = processCookbook(specificCookbook);

        if (cookbookDeprecated) {
            LOGGER.info("Cookbook \"" + cookbookName + "is deprecated");
        }
        return !cookbookDeprecated;
    }

    /**
     * Crawl a specific cookbook from Chef Supermarket by cookbook name and version restriction.
     *
     * @param cookbookName       This is the name of the requested cookbook.
     * @param versionRestriction Version restriction from metadata of a cookbook. Version perator and version must be
     *                           seperated by whitespace. If two restrictions they must be seperated by a "-" If one
     *                           restriction : "> 1.0.0" If two restrictions : ">1.0.0-< 2.1.3"
     * @return True when cookbook is crawled. False when not crawled because cookbook is deprecated.
     * @throws Exception On crawling error.
     */
    public boolean getCookbook(String cookbookName, String versionRestriction) throws Exception {
        boolean cookbookDeprecated;

        String supermarketApiRequest = getSupermarketApiRequest(cookbookName);
        JSONObject specificCookbook = getJsonFromUrl(supermarketApiRequest);

        cookbookDeprecated = processCookbook(specificCookbook, cookbookName, versionRestriction);

        if (cookbookDeprecated) {
            LOGGER.info("Cookbook \"" + cookbookName + "is deprecated");
        }
        return !cookbookDeprecated;
    }

    /**
     * Get a specific cookbook.
     *
     * @param cookbookJSONObject This is the JSON Object Response from Chef Supermarket Api.
     * @return Returns true if cookbook is deprecated, else false.
     * @throws Exception on processing Error.
     */
    private boolean processCookbook(JSONObject cookbookJSONObject) throws Exception {
        boolean cookbookDeprecated = false;

        cookbookDeprecated = cookbookJSONObject.getBoolean("deprecated");
        if (!cookbookDeprecated) {
            //Get name of cookbook
            String cookbookName = cookbookJSONObject.getString("name");
            //Get information to latest version of the cookbook.
            String latestVersionUrl = cookbookJSONObject.getString("latest_version");
            JSONObject latestVersionJSONObject = getJsonFromUrl(latestVersionUrl);
            //Get URL to latest version and version.
            String file = latestVersionJSONObject.getString("file");
            String cookbookVersion = latestVersionJSONObject.getString("version");
            //Download latest cookbook.
            try {
                downloadChefCookbook(cookbookName, cookbookVersion, file);
            } catch (Exception e) {
                LOGGER.error("Downloading of cookbook \"" + cookbookName + "-" + cookbookVersion + "failed", e);
            }
        }
        return cookbookDeprecated;
    }

    /**
     * Get a specific cookbook.
     *
     * @param cookbookJSONObject This is the JSON Object Response from Chef Supermarket Api.
     * @param cookbookName       This is the name of the cookbook to process.
     * @param versionRestriction The cookbook version restriciton from the metadata of a cookbook. Operator and Version
     *                           must me seperated by a whitespace.
     * @return Returns true if cookbook is deprecated, else false.
     * @throws Exception on processing Error.
     */
    private boolean processCookbook(JSONObject cookbookJSONObject, String cookbookName, String versionRestriction) throws Exception {
        boolean cookbookDeprecated = false;
        String extractedVersion;

        cookbookDeprecated = cookbookJSONObject.getBoolean("deprecated");
        if (!cookbookDeprecated) {
            JSONArray cookbooksToCrawlArray = cookbookJSONObject.getJSONArray("versions");

            for (int cbIndex = 0; cbIndex < cookbooksToCrawlArray.length(); cbIndex++) {
                String versionURL = (String) cookbooksToCrawlArray.get(cbIndex);
                extractedVersion = versionURL.substring(versionURL.lastIndexOf("/") + 1);

                boolean versionOk = false;
                Version cookbookAvailableVersion = new Version(extractedVersion);
                if (versionRestriction.indexOf("-") > 0) {
                    String[] parts = versionRestriction.split("-");
                    // Restriction has two parts seperated by a "-" and both have to be true;
                    if (parts.length == 2 && isVersionOk(parts[0], cookbookAvailableVersion) && isVersionOk(parts[1], cookbookAvailableVersion)) {
                        versionOk = true;
                    }
                } else {
                    versionOk = isVersionOk(versionRestriction, cookbookAvailableVersion);
                }

                if (versionOk) {
                    JSONObject latestVersionJSONObject = getJsonFromUrl(versionURL);
                    //Get URL to latest version and version.
                    String file = latestVersionJSONObject.getString("file");
                    String cookbookVersion = latestVersionJSONObject.getString("version");

                    try {
                        downloadChefCookbook(cookbookName, cookbookVersion, file);
                    } catch (Exception e) {
                        LOGGER.error("Downloading of cookbook \"" + cookbookName + "-" + cookbookVersion + "failed", e);
                    }
                    break;
                }
            }
        }
        return cookbookDeprecated;
    }

    /**
     * Method provides functionality to compare cookbook versions
     *
     * @param versionRestriction       The cookbook version restriciton from the metadata of a cookbook. Operator and
     *                                 Version must me seperated by a whitespace.
     * @param cookbookAvailableVersion The cookbook version to compare with
     * @return Return true if cookbook version fits into the version Restriction.
     */
    private boolean isVersionOk(String versionRestriction, Version cookbookAvailableVersion) {
        Version cookbookDependencie;
        boolean versionOk = false;
        String[] parts = versionRestriction.split("\\s+");
        String operator = parts[0];
        String rawVersion = parts[1];
        cookbookDependencie = new Version(rawVersion);

        switch (operator) {
            case ">":
                if (cookbookAvailableVersion.compareTo(cookbookDependencie) == 1) {
                    versionOk = true;
                }
                break;
            case ">=":
                if (cookbookAvailableVersion.compareTo(cookbookDependencie) >= 0) {
                    versionOk = true;
                }
                break;
            case "=":
                if (cookbookAvailableVersion.compareTo(cookbookDependencie) == 0) {
                    versionOk = true;
                }
                break;
            case "<=":
                if (cookbookAvailableVersion.compareTo(cookbookDependencie) <= 0) {
                    versionOk = true;
                }
                break;
            case "<":
                if (cookbookAvailableVersion.compareTo(cookbookDependencie) == -1) {
                    versionOk = true;
                }
                break;
        }
        return versionOk;
    }

    /**
     * Read the JSON Response from an API call.
     *
     * @param url This is the URL to read from.
     * @return Returns the response of the request.
     * @throws JSONException on JSON parsing failure.
     */
    private static JSONObject getJsonFromUrl(String url) throws IOException, JSONException {
        try (InputStream is = new URL(url).openStream()) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")))) {
                String jsonText = readAll(reader);
                JSONObject json = new JSONObject(jsonText);
                return json;
            }
        }
    }

    /**
     * Read all lines from a Buffered Reader.
     *
     * @param br This is the Buffered Reader.
     * @return Returns all lines appended in a String.
     * @throws IOException on reading failure
     */
    private static String readAll(BufferedReader br) throws IOException {
        String responseString = "";
        String output;

        while ((output = br.readLine()) != null) {
            responseString = responseString + output;
        }
        return responseString;
    }

    /**
     * Download a cookbook from a download link. Save the url as .tgz in a temp directory and decompress it to the
     * cookbooks directory.
     *
     * @param cookbookName This is name of the cookbook to download.
     * @param version      This is the version of the downloaded cookbook to create a filename.
     * @param filename     This is the url to the file where cookbook is downloaded from.
     * @throws Exception on download failure.
     */
    private void downloadChefCookbook(String cookbookName, String version, String filename) throws Exception {
        URL url = new URL(filename);
        HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
        int responseCode = httpConnection.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            String fileName = "";

            //Content-Disposition always null
            String disposition = httpConnection.getHeaderField("Content-Disposition");

            if (disposition != null) {
                // extracts file name from header field
                int index = disposition.indexOf("filename=");
                if (index > 0) {
                    fileName = disposition.substring(index + 10,
                        disposition.length() - 1);
                }
            } else {
                fileName = filename.substring(fileName.lastIndexOf("/") + 1,
                    fileName.length());
            }

            InputStream inputStream = httpConnection.getInputStream();
            String saveCookbookPath = this.tempDirectory + "/" + cookbookName + "-" + version + ".tgz";

            FileOutputStream outputStream = new FileOutputStream(saveCookbookPath);

            int bytesRead;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();

            String cookbookDirectory = this.cookbookDirectory + "/";

            try {
                extractCookbook(saveCookbookPath, new File(cookbookDirectory));
            } catch (IOException e) {
                e.printStackTrace();
            }

            LOGGER.info(cookbookName + " downloaded and decompressed");
        } else {
            LOGGER.error("No file to download. Server replied HTTP code: " + responseCode);
        }
        httpConnection.disconnect();
    }

    /**
     * Get the Chef Supermarket API Request for requesting a certain amount of cooksbooks beginning from an offset.
     *
     * @param startItem This is the offset into a list of tools, at which point the list of tools will begin.
     * @param items     The number of items to be returned as a result of the request.
     * @return This returns the API Request.
     */
    private static String getSupermarketApiRequest(int startItem, int items) {
        return "https://supermarket.chef.io/api/v1/cookbooks?start=" + startItem + "&items=" + items;
    }

    /**
     * Get the Chef Supermarket API Request for requesting a specific cookbook.
     *
     * @param cookbookName This is the name of the requested cookbook.
     * @return This returns the API Request for a specific cookbook.
     */
    private static String getSupermarketApiRequest(String cookbookName) {
        return "https://supermarket.chef.io/api/v1/cookbooks/" + cookbookName;
    }

    /**
     * Extracts a raw cookbook which can be a .tar or a .tgz file.
     *
     * @param in  This is the path to the file which is decompressed.
     * @param out This is the output file where decompressed cookbook is saved.
     * @throws IOException on decompress failure
     */
    private static void extractCookbook(String in, File out) throws Exception {
        try (TarArchiveInputStream fin = new TarArchiveInputStream(new GzipCompressorInputStream(new FileInputStream(in)))) {
            decompressTarArchive(out, fin);
        } catch (IOException e) {
            TarArchiveInputStream fin = new TarArchiveInputStream(new FileInputStream(in));
            decompressTarArchive(out, fin);
        }
    }

    /**
     * Decompresses a TarArchive
     *
     * @param out This is the output file where the decompressed archive is saved.
     * @param fin This is the Tar Archive to decopress.
     * @throws IOException on decompress failure.
     */
    private static void decompressTarArchive(File out, TarArchiveInputStream fin) throws IOException {
        TarArchiveEntry entry;
        while ((entry = fin.getNextTarEntry()) != null) {
            if (entry.isDirectory()) {
                continue;
            }

            File curfile = new File(out, entry.getName());
            File parent = curfile.getParentFile();
            if (!parent.exists()) {
                parent.mkdirs();
            }
            IOUtils.copy(fin, new FileOutputStream(curfile));
        }
    }

    private void initCrawler(String cookbookDirectory, String tempDirectory) {
        createDirectory(tempDirectory);
        createDirectory(cookbookDirectory);
    }

    private static void createDirectory(String directoy) {
        if (!Files.exists(Paths.get(directoy))) {
            boolean successDir = (new File(directoy)).mkdirs();
            if (!successDir) {
                LOGGER.error("Creating folder \"" + directoy + "went wrong!");
            }
        }
    }
}
