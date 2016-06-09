package com.pmpulse.serviceutil;

import com.pmpulse.data.KeyValues;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.URLConnection;


public class Request {
    static String registerString;

    public URLConnection login(URLConnection con, String email, String password) {
        //JSONObject registerJSON = new JSONObject();

        try {
            JSONObject register = new JSONObject();
            register.put("email", email);
            register.put("password", password);
            //	//register.put("userName", StaticVariables.userName);

            //	registerJSON.put("registrationRequest", register);
            //registerString = registerJSON.toString();
            registerString = register.toString();
            if (KeyValues.isDebug)
                System.out.println(registerString);
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Content-Length", registerString.length()
                    + "");
            con.setConnectTimeout(KeyValues.TIMEOUT);
            con.setDoOutput(true);

            OutputStream os = con.getOutputStream();
            os.write(registerString.getBytes());
            os.flush();
        } catch (Exception e) {
            if (KeyValues.isDebug)
                System.out.println("in req general" + e.getMessage());
            registerString = null;

        }
        return con;
    }
}