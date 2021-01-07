/*
 * Copyright (c) Microsoft. All rights reserved. Licensed under the MIT license.
 * See LICENSE in the project root for license information.
 */
package smarter.uearn.money.office365;

public interface Constants {
    String AUTHORITY_URL = "https://login.microsoftonline.com/common";
    String DISCOVERY_RESOURCE_URL = "https://api.office.com/discovery/v1.0/me/";
    String DISCOVERY_RESOURCE_ID = "https://graph.microsoft.com/";
    //String DISCOVERY_RESOURCE_ID2 = "https://api.office.com/discovery/";
    String MAIL_CAPABILITY = "Mail";
    // Update these two constants with the values for your application:
    //String CLIENT_ID = "f706ad3e-0243-4761-8ddf-deefc2237dbd";
    String CLIENT_ID = "faf300a9-ce06-4089-b038-5af178ec2516";
    String DRIVE_ID = "https://graph.microsoft.com/v1.0/me/drive";
    //String REDIRECT_URI = "https://smartersmbo365.azurewebsites.net";
    //Modified
    /*String REDIRECT_URI = "http://localhost:3000/callback";*/
    String REDIRECT_URI = "http://localhost:3000/auth/azureadoauth2/callback";
}
