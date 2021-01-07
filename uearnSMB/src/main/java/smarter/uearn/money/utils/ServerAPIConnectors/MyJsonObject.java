package smarter.uearn.money.utils.ServerAPIConnectors;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kavya on 10/1/2015.
 */
public class MyJsonObject extends JSONObject{

    public MyJsonObject(String json) throws JSONException {
        super(json);
    }

    @Override
    public String getString(String name) {
        try {
            if(super.getString(name).equals("null"))
                return "";
            else
                return super.getString(name);
        }catch (JSONException json){
            return "";
        }
    }
}
