package com.ningso.libcurl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * Created by walexy on 15/1/31.下午5:39
 *
 * @author walexy
 */
public class CurlResult {

    private static final String TAG = CurlResult.class.getSimpleName();
    private final int status;
    private final String statusLine;
    private final Map<String, String> headers;
    private final byte[] body;
    private transient String bodyString;
    private transient byte[] decodedBody;

    public CurlResult(int status, String statusLine, Map<String, String> headers, byte[] body) {
        super();
        this.status = status;
        this.statusLine = statusLine;
        this.headers = headers;
        this.body = body;
    }

    public int getStatus() {
        return status;
    }

    //may contains \n at end
    public String getStatusLine() {
        return statusLine;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getHeader(String header) {
        return headers.get(header);
    }

    //original body
    public byte[] getBody() {
        return body;
    }

    //decoded if body gzipped
    public byte[] getDecodedBody() throws IOException {
        if (!"gzip".equalsIgnoreCase(getHeader("Content-Encoding"))) {
            return body;
        }
        if (decodedBody == null) {
//			Log.d(TAG, "uncompress gzipped content");
            GZIPInputStream gzis = new GZIPInputStream(new ByteArrayInputStream(body));
            ByteArrayOutputStream byos = new ByteArrayOutputStream(body.length * 3);
            byte[] buf = new byte[4096];
            int len;
            while ((len = gzis.read(buf, 0, buf.length)) != -1) {
                byos.write(buf, 0, len);
            }
            decodedBody = byos.toByteArray();
            gzis.close();
            byos.close();
        }

        return decodedBody;
    }

    //IOException
    public String getBodyAsString() throws IOException {
        if (bodyString == null) {
            bodyString = new String(getDecodedBody());
        }
        return bodyString;
    }
}
