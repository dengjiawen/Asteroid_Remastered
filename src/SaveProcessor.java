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

        System.out.println("Attempting to find save file in remote cloud...");

        //ArrayList for holding String downloaded from save file
        ArrayList<String> save_content = new ArrayList<>();

        /* Create InputStream from
         *
         */
        try {
            InputStream input = save_file_directory.openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String line;

            while ((line = reader.readLine()) != null) {
                save_content.add(line);
            }

            input.close();

        } catch (Exception e) {
            SaveManager.firstTimeInit();
            return 0;
        }


        save_content.forEach(string -> {
            string = StringEncryptor.decoder(string);

            if (string.contains("#")) {
                save_content.remove(string);
            }

        });

        return Integer.parseInt(save_content.get(0).substring(10));

    }


}

class StringEncryptor {

    public static String encryptor(String original_string){

        char[] temp_array = original_string.toCharArray();

        for (int i = 0; i < original_string.length(); i++){
            temp_array[i] = (char)(temp_array[i] + 1);
        }

        return String.valueOf(temp_array);

    }

    public static String decoder(String target_string){

        char[] temp_array = target_string.toCharArray();

        for (int i = 0; i < target_string.length(); i++){
            temp_array[i] = (char)(temp_array[i] - 1);
        }

        return String.valueOf(temp_array);

    }

}

class SaveManager{

    private static final String SERVER = "185.176.43.78";
    private static final String USERNAME = "2368344";
    private static final String PASSWORD = "dengjiawen";

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

    public static void uploadSave(File save_file){

        FTPClient ftp = new FTPClient();

        try {

            ftp.connect(SERVER,21);
            ftp.login(USERNAME,PASSWORD);

            ftp.changeDirectory("/asteroidsave.royalwebhosting.net/game_save");
            ftp.upload(save_file);

            ftp.disconnect(true);

        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public static void firstTimeInit(){

        boolean activated = false;

        while (!activated){
            activated =
                    activationCheck(
                    JOptionPane.showInputDialog(null,
                            "Please enter your game activation code.",
                            "Activation", JOptionPane.INFORMATION_MESSAGE)
                    );

            if (!activated){
                JOptionPane.showMessageDialog(null,
                        "Your activation code is invalid.",
                        "Invalid Activation", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null,
                        "Your game had been activated.",
                        "Successful Activation", JOptionPane.INFORMATION_MESSAGE);
            }
        }

        Resources.top_score = 0;
        uploadSave(createSave(Resources.top_score));

    }

    public static boolean activationCheck(String code){

        try {
            InputStream input = new URL("http://asteroidsave.royalwebhosting.net/default_keys.txt").openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String line;

            while ((line = reader.readLine()) != null) {
                if (StringEncryptor.decoder(line).equals(code)){
                    return true;
                }
            }

        } catch (Exception e){
            return false;
        }

        return false;

    }

    public static File createSave(int topScore){

        File tempFile = null;

        try{

            tempFile = new File(System.getProperty("java.io.tmpdir") + "/" + Resources.UDID + ".txt");
            tempFile.getParentFile().mkdirs();
            PrintWriter writer = new PrintWriter(tempFile);

            for (String x : SAVE_HEADER){
                writer.println(StringEncryptor.encryptor(x));
            }

            writer.println(StringEncryptor.encryptor("TOP_SCORE " + topScore));

            writer.close();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "An error occured while saving files. Your game data will not be saved.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        return tempFile;

    }

}
