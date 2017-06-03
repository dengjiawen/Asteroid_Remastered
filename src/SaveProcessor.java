/**
 * Copyright 2017 (C) Jiawen Deng
 * ALL RIGHTS RESERVED
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *
 *     Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * Created on : 25-05-2017
 * Author     : Jiawen Deng
 *
 *-----------------------------------------------------------------------------
 * Revision History (Release 1.0)
 *-----------------------------------------------------------------------------
 * VERSION     AUTHOR/      DESCRIPTION OF CHANGE
 * OLD/NEW     DATE
 *-----------------------------------------------------------------------------
 * --/0.1  | J.D.          | Initial creation of program
 *         | 25-05-17      |
 *---------|---------------|---------------------------------------------------
 * 0.1/0.2 | J.D.          | Offloaded string en/decrypt functionality into
 *         | 29-05-17      | generalized & encapsulated methods.
 *---------|---------------|---------------------------------------------------
 * 0.2/0.3 | J.D.          | Modified SaveProcessor class so that all methods
 *         | 31-05-17      | now take URL as parameters instead of File.
 *---------|---------------|---------------------------------------------------
 * 0.3/0.4 | J.D.          | Added SaveManager class to manage FTP file
 *         | 02-06-17      | uploading.
 *---------|---------------|---------------------------------------------------
 * 0.4/1.0 | J.D.          | Major update: added activation feature, and
 *         | 02-06-17      | first time setup methods.
 *---------|---------------|---------------------------------------------------
 *
 * This is a piece of custom code with a custom algorithm for encrypting and
 * decrypting strings. By shifting every single character in the string by one
 * unit (Example: a -> b, H -> I, 1 -> 2), this algorithm effectively renders
 * game save files unreadable/meaningless.
 *
 * This class also contain various methods for uploading & encrypting,
 * downloading & decrypting save files for the game, as well as activation
 * and first time setup methods for new players.
 *
 * The save file contains the player's top score.
 *
 */

import it.sauronsoftware.ftp4j.FTPClient;

import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;

class SaveProcessor {

    /**
     * Method for downloading and processing save files in order to read
     * the player's top score.
     * @param save_file_directory take in URL pointing to remote save file
     * @return integer containing player's top score
     */
    public static int saveDecoder(URL save_file_directory) {

        Resources.outputSeperator();
        System.out.println("Attempting to find save file in remote cloud...");

        //ArrayList for holding String downloaded from save file
        ArrayList<String> save_content = new ArrayList<>();

        /* Create InputStream from downloaded save file
         * If save file does not exist, check server
         * connection; if connected, start first time
         * setup.
         */
        try {
            InputStream input = save_file_directory.openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            // ^^ InputStream and BufferedReader to read remote save file
            String line;
            // ^^ Temporary String for holding each line of the save

            System.out.println("Save files found with the following content:");
            Resources.outputSeperator();

            /* While save file have new lines, save to line
             * than add to save_content ArrayList
             */
            while ((line = reader.readLine()) != null) {
                save_content.add(line);
                System.out.println(line);
            }

            //close InputStream when done
            input.close();

        } catch (Exception e) {

            /* Exceptions only occur under two conditions:
             *
             * 1. File do not exist on remote servers:
             *      This means that the user is playing
             *      for the very first time; start
             *      first time setup process.
             *
             * 2. Connection had been dropped:
             *      Check connections.
             */

            System.out.println("Save files not found. Checking connection.");

            if (Bootstrap.checkConnection()) {      //if connection is OK
                System.out.println("Connection OK. Initiating first time setup.");
                SaveManager.firstTimeInit(false);       //Assume player's game is not activated

                //Return 0 as top score, since this is user's first time playing
                return 0;

            } else {        //if connection failed
                System.out.println("Connection failed.");
                Bootstrap.connectionError();
            }
        }

        // If save file is found, load and decode.

        Resources.outputSeperator();
        System.out.println("Save files decoded. Content:");
        Resources.outputSeperator();

        /* Run through the content array, remove comments
         * which are marked with #.
         *
         * Because all saves are on cloud and saved in a
         * controlled manner, it is assumed that any line
         * of save without # is the line containing the
         * top score information.
         *
         * If this is not the case, assume that save had
         * been corrupted (most likely due to incomplete
         * upload to server). Inform user that their
         * game data will be reset.
         */
        for (int i = 0; i < save_content.size(); i++) {
            save_content.set(i, StringEncryptor.decrypt(save_content.get(i)));

            System.out.println(save_content.get(i));

            if (save_content.get(i).contains("#")) {
                save_content.remove(i);
                System.out.println("Entry is comment: removed.");
            } else {

                Resources.outputSeperator();
                System.out.println("Found top score entry.");

                try {
                    return Integer.parseInt(save_content.get(i).substring(10));     //Return the top score
                } catch (Exception e){

                    System.out.println("Entry error. Possible save corruption.");

                    //Pop-up to inform user of corrupted save.
                    JOptionPane.showMessageDialog(null,
                            "The cloud save had corrupted. Your save will be reset.",
                            "Error", JOptionPane.INFORMATION_MESSAGE);

                    System.out.println("Resetting player save file.");
                    SaveManager.firstTimeInit(true);
                }
            }
        }

        //Return statement to combat compiler error
        return 0;

    }

}

class StringEncryptor {

    /**
     * Method for encrypting Strings.
     * @param original_string Takes the target String as parameter.
     * @return encrypted version of the String.
     */
    public static String encrypt(String original_string){

        Resources.outputSeperator();
        System.out.println("Initializing String Encryptor...");

        char[] temp_array = original_string.toCharArray();      //Split string into char array

        System.out.println("Converting String " + original_string);

        /* Shift every char in the String up by 1 on the ASCII table.
         * This effectively scrambles the characters.
         */
        for (int i = 0; i < original_string.length(); i++){
            temp_array[i] = (char)(temp_array[i] + 1);
        }

        System.out.println("String conversion successful: " + String.valueOf(temp_array));
        Resources.outputSeperator();

        //Reassemble the scrambled String and return it.
        return String.valueOf(temp_array);

    }

    /**
     * Method for decrypting the encrypted Strings.
     * @param target_string Takes the target String as parameter.
     * @return decrypted version of the String.
     */
    public static String decrypt(String target_string){

        Resources.outputSeperator();
        System.out.println("Initializing String Decryptor...");

        char[] temp_array = target_string.toCharArray();        //Split string into char array

        System.out.println("Decrypting String " + target_string);

        /* Shift every char in the String down by 1 on the ASCII table.
         * This effectively restore the characters.
         */
        for (int i = 0; i < target_string.length(); i++){
            temp_array[i] = (char)(temp_array[i] - 1);
        }

        System.out.println("String decryption successful: " + String.valueOf(temp_array));
        Resources.outputSeperator();

        //Reassemble the restored String and return it.
        return String.valueOf(temp_array);

    }

}

class SaveManager{

    /**
     * The purpose of this class is to write and upload save files
     * to a dedicated remote FTP server, using UDID as file name.
     */

    //Access details for the FTP server.
    private static final String SERVER = "185.176.43.78";       //FTP server IP address
    private static final String USERNAME = "2368344";           //FTP server login username
    private static final String PASSWORD = "dengjiawen";        //FTP server login password

    //Standard save header that is written to the beginning of evey save file.
    private static final String[] SAVE_HEADER = new String[]{
            "# This is the configuration for \"Asteroid Remastered\"",
            "# Revision 11.07.2017",
            "# My crush is S.T.",
            "# Copyright 2017 (C) Jiawen Deng",
            "# ALL RIGHTS RESERVED",
            "# Redistribution and use in source and binary forms, with or without" +
                    "modification, are permitted provided that the following conditions are met:",
            "# Redistributions of source code must retain the above copyright notice, " +
                    "this list of conditions and the following disclaimer.",
            "# Redistributions in binary form must reproduce the above copyright notice, " +
                    "this list of conditions and the following disclaimer in the documentation" +
                    "and/or other materials provided with the distribution."
    };

    /**
     * Method for uploading the save file to the FTP server.
     * @param save_file Takes the save file as the parameter.
     */
    private static void uploadSave(File save_file){

        Resources.outputSeperator();
        System.out.println("Initializing FTP client for save upload...");

        FTPClient ftp = new FTPClient();        //Initialize new FTP connection

        try {

            System.out.println("Connecting to FTP server...");

            //Connect to server using host information
            ftp.connect(SERVER,21);
            ftp.login(USERNAME,PASSWORD);

            System.out.println("Successfully connected to FTP @ " + SERVER + ".");
            System.out.println("Preparing file for upload...");
            System.out.println("Uploading file...");

            //Switch to dedicated save storage directory & upload file
            ftp.changeDirectory("/asteroidsave.royalwebhosting.net/game_save");
            ftp.upload(save_file);

            System.out.println("File successfully uploaded.");

            //Disconnect from FTP when done.
            ftp.disconnect(true);

            System.out.println("Disconnected from FTP server.");
            Resources.outputSeperator();

        } catch (Exception e){

            /* There will only be an error if Internet connection
             * is down. The FTP server is 24/7.
             *
             * If error occurs, check for connection, then try again.
             */

            System.out.println("Connection to FTP failed.");
            System.out.println("Checking network connection...");

            if (Bootstrap.checkConnection()){
                System.out.println("Connection OK. Retrying...");
                uploadSave(save_file);
            } else {
                System.out.println("Connection failed.");
                Bootstrap.connectionError();
            }

        }

    }

    /**
     * Method for walking the user through the game activation
     * and save creation process.
     * @param activated Takes boolean to check whether the game
     *                  had already been activated previously.
     */
    public static void firstTimeInit(boolean activated){

        Resources.outputSeperator();
        System.out.println("Initializing first time setup process...");
        System.out.println("Waiting for user to input activation information...");

        /* If activated boolean is not set as true,
         * ask user for activation code.
         */

        // IF USER DON'T HAVE A VALID CODE, THEY WON'T BE ABLE TO EXIT THE PROGRAM.
        // THIS IS NOT A CODING ERROR; THE SOFTWARE IS DESIGNED THIS WAY.

        while (!activated){
            activated =
                    activationCheck(
                    JOptionPane.showInputDialog(null,
                            "Please enter your game activation code.",
                            "Activation", JOptionPane.INFORMATION_MESSAGE)
                    );

            //Checking user's activation code.

            if (!activated){

                //If code is not valid, display message.

                JOptionPane.showMessageDialog(null,
                        "Your activation code is invalid.",
                        "Invalid Activation", JOptionPane.ERROR_MESSAGE);
            } else {

                //If code is valid, display message.

                JOptionPane.showMessageDialog(null,
                        "Your game had been activated.",
                        "Successful Activation", JOptionPane.INFORMATION_MESSAGE);
            }
        }

        System.out.println("Activation complete.");
        System.out.println("Initializing top score...");
        System.out.println("Creating and uploading save data...");

        Resources.top_score = 0;
        uploadSave(createSave(Resources.top_score));

        Resources.outputSeperator();

    }

    /**
     * Method for checking the activation code, comparing
     * them against the record kept on the remote
     * FTP server.
     * @param code Takes the activation code entered by
     *              the user as a parameter.
     * @return boolean indicating whether the activation
     *         code had been accepted.
     */
    private static boolean activationCheck(String code){

        Resources.outputSeperator();
        System.out.println("Initializing activation checker...");
        System.out.println("Loading activation codes from cloud...");

        /* Create InputStream from activation record stored
         * on the remote FTP server.
         */
        try {
            InputStream input = new URL("http://asteroidsave.royalwebhosting.net/default_keys.txt").openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            // ^^ InputStream and BufferedReader to read remote save file
            String line;
            // ^^ Temporary String for holding each recorded activation code

            /* check every single recorded key against
             * user intput. If one of the keys match,
             * return true.
             */
            while ((line = reader.readLine()) != null) {
                if (StringEncryptor.decrypt(line).equals(code)){        //Recorded keys are encrypted; decryption required

                    System.out.println("Activation code " + code + " is valid.");

                    return true;
                }
            }

        } catch (Exception e){

            /* If an exception occured, check connection.
             *
             * If connection is fine, treat error as a
             * freak error and return false for user to
             * try again.
             *
             * If connection failed, display connection
             * error screen.
             */

            System.out.println("Activation process had encountered an error.");
            System.out.println("Checking connection to activation server...");

            if (Bootstrap.checkConnection()) {

                System.out.println("Connection is OK.");
                System.out.println("An unexpected error had occured.");
                System.out.println("Error handled. Asking user to enter code again.");

                return false;
            } else {

                System.out.println("Connection failed. Cannot connect to activation server.");

                Bootstrap.connectionError();
            }

        }

        /* If true had not been returned by this point,
         * assume that activation code is invalid and
         * return false.
         */

        System.out.println("Activation code " + code + " is invalid.");

        return false;

    }

    /**
     * Method for generating a save file in the temporary
     * directory on local storage.
     * @param topScore Takes an Integer containing the top
     *                 score as a parameter; writing the
     *                 top score into the save file.
     * @return file pointing to the save file.
     */
    public static File createSave(int topScore){

        Resources.outputSeperator();
        System.out.println("Generating save file...");

        //Initialize File pointing to temporary save
        File tempFile = null;

        /* Try to create a temporary save file in the system's temporary directory.
         * Print save header and top score information into the file using PrintWriter.
         */
        try{

            System.out.println("Preparing to write temporary save file to " + System.getProperty("java.io.tmpdir") + ".");

            tempFile = new File(System.getProperty("java.io.tmpdir") + "/" + Resources.UDID + ".txt");
            tempFile.getParentFile().mkdirs();      //In case file is non-existent, create it.
            PrintWriter writer = new PrintWriter(tempFile);

            System.out.println("Writing save header...");

            //For every line on the save header array, write to file
            for (String x : SAVE_HEADER){
                writer.println(StringEncryptor.encrypt(x));
                System.out.println(x);
            }

            System.out.println("Writing top score...");

            //Write top score.
            writer.println(StringEncryptor.encrypt("TOP_SCORE " + topScore));

            System.out.println("Write success!");
            System.out.println("Closing writer.");

            //Close writer when done.
            writer.close();

        } catch (IOException e) {

            /* If an error had occured, inform the user that
             * their progress will not be saved.
             */

            System.out.println("Error while saving.");

            JOptionPane.showMessageDialog(null,
                    "An error occured while saving files. Your game data will not be saved.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        System.out.println("Sending save file to be uploaded.");
        Resources.outputSeperator();

        //return the file.
        return tempFile;

    }

}
