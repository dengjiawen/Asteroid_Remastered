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
 * Created on : 05-03-2017
 * Author     : Jiawen Deng
 *
 *-----------------------------------------------------------------------------
 * Revision History (Release 0.1)
 *-----------------------------------------------------------------------------
 * VERSION     AUTHOR/      DESCRIPTION OF CHANGE
 * OLD/NEW     DATE
 *-----------------------------------------------------------------------------
 * --/0.1  | J.D.          | Initial creation of program
 *         | 05-03-17      |
 *---------|---------------|---------------------------------------------------
 *
 * A simple program created in March of 2017, intended for testing the
 * possibility of generating a device unique identification number by
 * using the user's MAC address.
 * Edited to accomodate Asteroid: Remastered.
 *
 */

import java.net.InetAddress;
import java.net.NetworkInterface;

public class IDGenerator {

    /** static method used to generate UDID (Universal Device Identification Number)
     *  using the user's MAC address.
     *
     * @return UDID in the form of String
     */

    public static String UDIDGenerator(){

        String UDID;       //String holding UDID

        Resources.outputSeperator();
        System.out.println("Generating UDID...");
        try {
            /* Retrieve MAC address from network hardware interface
             * Store MAC address in byte array
             */
            InetAddress address = InetAddress.getLocalHost();                   //Retrieve IP Address
            NetworkInterface nwi = NetworkInterface.getByInetAddress(address);  //Retrieve network INTF using IP
            byte mac[] = nwi.getHardwareAddress();

            /* Use StringBuilder to structure MAC address
             * into String with proper structure
             */
            StringBuilder sb = new StringBuilder();
            for (byte aMac : mac) {
                sb.append(String.format("%02X%s", aMac, ""));
            }
            UDID = sb.toString().toLowerCase();
        }
        catch(Exception e) {
            //If an error occured, return default UDID
            return "000000";
        }

        //Return UDID
        return UDID;

    }

}
