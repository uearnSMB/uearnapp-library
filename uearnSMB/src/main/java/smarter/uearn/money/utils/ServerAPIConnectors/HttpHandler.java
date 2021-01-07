package smarter.uearn.money.utils.ServerAPIConnectors;

/**
 * Created by Srinath on 9/7/2016.
 * Used In Secure APIS
 */
public class HttpHandler {

    /*private static HttpClient mHttpClient;
    private static HttpContext mHttpContext;

    public static Cookie mSessionCookie;
    static String TAG="CustomHttpClient";
    static int HTTP_TIMEOUT = 3000;

    Cookie sessionCookie;
    CookieManager cookieManager;

    public static HttpClient getHttpClient() {
        if (mHttpClient == null) {
            mHttpClient = new DefaultHttpClient();
            final HttpParams params = mHttpClient.getParams();

            HttpConnectionParams.setConnectionTimeout(params, HTTP_TIMEOUT);
            HttpConnectionParams.setSoTimeout(params, HTTP_TIMEOUT);
            ConnManagerParams.setTimeout(params, HTTP_TIMEOUT);
        }
        return mHttpClient;
    }
    private static HttpContext getHttpContext(){
        if(mHttpContext==null){
            mHttpContext=new BasicHttpContext();
        }
        return mHttpContext;
    }

    public static String executeHttpPost( String url, JSONObject postParameters) throws Exception {
        BufferedReader in = null;
        try {
            HttpClient client = getHttpClient();

            CookieStore basicCookieStore = new BasicCookieStore();
            HttpContext localContext = getHttpContext();
            if(ApplicationSettings.getPref("cookie",null) != null) {
                 basicCookieStore = getCookieStore(ApplicationSettings.getPref("cookie", null), "http://smarter-biz.com/");
            }
            localContext.setAttribute(ClientContext.COOKIE_STORE, basicCookieStore);

            HttpPost request = new HttpPost(url);
            request.addHeader("content-type", "application/json");

            StringEntity se = new StringEntity(postParameters.toString());

            request.setEntity(se);
            HttpResponse response = client.execute(request, localContext);
            //HttpResponse response = client.execute(request);
            Log.e("headers", "" + response.headerIterator());
            //String mSessionId=null;
            //  Log.e("mSessionId",""+mSessionId);
            HeaderElementIterator it = new BasicHeaderElementIterator(
                    response.headerIterator());
            while (it.hasNext()) {
                HeaderElement elem = it.nextElement();

                if (elem.getName().equals("JSESSIONID")) {
                    //NetworkManager.mSessionId = elem.getValue();
                }
                NameValuePair[] params = elem.getParameters();
                for (int i = 0; i < params.length; i++) {

                }
            }

            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            StringBuffer sb = new StringBuffer("");
            String line = "";
            String NL = System.getProperty("line.separator");
            while ((line = in.readLine()) != null) {
                sb.append(line + NL);
            }
            in.close();

            String result = sb.toString();
            List<Cookie> cookies = basicCookieStore.getCookies();
            Log.e("CustomHttpClient","Cookies size= " + cookies.size());
            for (int i = 0; i < cookies.size(); i++) {
                Cookie cookie = cookies.get(i);
                Log.e("CustomHttpClient","Local cookie: " + cookie);
                mSessionCookie = cookie;
                Log.e("CustomHttpClient",""+cookie.getValue());
            }

            return result;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String executeHttpGet(String url) throws Exception {
        BufferedReader in = null;
        CookieManager cookieManager = CookieManager.getInstance();
        try {
            HttpClient client = getHttpClient();
            HttpGet request = new HttpGet();
            //Newly Added :::KSN
            HttpContext localContext=getHttpContext();
            request.setURI(new URI(url));
            HttpResponse response = client.execute(request,localContext);
            if(ApplicationSettings.getPref("cookie",null) == null) {
                cookieManager.removeAllCookie();
                String cookies = CookieManager.getInstance().getCookie(url);
                saveCookie(cookies);
                BasicCookieStore lCS = getCookieStore(cookies, "http://smarter-biz.com/");
            } else {
                BasicCookieStore lCS = getCookieStore(ApplicationSettings.getPref("cookie",null), "http://smarter-biz.com/");
            }
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            StringBuffer sb = new StringBuffer("");
            String line = "";
            String NL = System.getProperty("line.separator");
            while ((line = in.readLine()) != null) {
                sb.append(line + NL);
            }
            in.close();
            Log.e("headers",""+response.headerIterator());
            HeaderElementIterator it = new BasicHeaderElementIterator(
                    response.headerIterator());
            while (it.hasNext()) {
                HeaderElement elem = it.nextElement();

                if(elem.getName().equals("JSESSIONID")){
                   // NetworkManager.mSessionId=elem.getValue();
                }
                NameValuePair[] params = elem.getParameters();
                for (int i = 0; i < params.length; i++) {

                }
            }

            String result = sb.toString();
            return result;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static BasicCookieStore getCookieStore(String cookies, String domain) {
        String[] cookieValues = cookies.split(";");
        BasicCookieStore cs = new BasicCookieStore();

        BasicClientCookie cookie;
        for (int i = 0; i < cookieValues.length; i++) {
            String[] split = cookieValues[i].split("=");
            if (split.length == 2)
                cookie = new BasicClientCookie(split[0], split[1]);
            else
                cookie = new BasicClientCookie(split[0], null);

            cookie.setDomain(domain);
            cs.addCookie(cookie);
        }
        return cs;
    }

    public static void saveCookie( String cookie) {
        if(cookie != null) {
                ApplicationSettings.putPref("cookies",cookie);
        }
    }*/
}

