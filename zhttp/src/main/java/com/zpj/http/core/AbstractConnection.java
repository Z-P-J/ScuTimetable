package com.zpj.http.core;

import com.zpj.http.parser.html.Parser;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.http.utils.UrlUtil;
import com.zpj.http.utils.Validate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.Collection;
import java.util.Map;

import javax.net.ssl.SSLSocketFactory;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public abstract class AbstractConnection implements Connection {

    //    protected IHttp.OnRedirectListener onRedirectListener;
//    protected IHttp.OnSubscribeListener onSubscribeListener;
//    protected IHttp.OnSuccessListener onSuccessListener;
//    protected IHttp.OnErrorListener onErrorListener;
//    protected IHttp.OnCompleteListener onCompleteListener;


    protected final Request req;
    protected Response res;

    public AbstractConnection() {
        req = createRequest();
//        res = createResponse();
    }

    @Override
    public Connection url(URL url) {
        req.url(url);
        return this;
    }

    @Override
    public Connection url(String url) {
        Validate.notEmpty(url, "Must supply a valid URL");
        try {
            req.url(new URL(UrlUtil.encodeUrl(url)));
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Malformed URL: " + url, e);
        }
        return this;
    }

    @Override
    public Connection proxy(Proxy proxy) {
        req.proxy(proxy);
        return this;
    }

    @Override
    public Connection proxy(String host, int port) {
        req.proxy(host, port);
        return this;
    }

    @Override
    public Connection timeout(int millis) {
        req.timeout(millis);
        return this;
    }

    @Override
    public Connection maxBodySize(int bytes) {
        req.maxBodySize(bytes);
        return this;
    }

    @Override
    public Connection method(Method method) {
        req.method(method);
        return this;
    }

    @Override
    public Connection ignoreHttpErrors(boolean ignoreHttpErrors) {
        req.ignoreHttpErrors(ignoreHttpErrors);
        return this;
    }

    @Override
    public Connection ignoreContentType(boolean ignoreContentType) {
        req.ignoreContentType(ignoreContentType);
        return this;
    }

    @Override
    public Connection validateTLSCertificates(boolean value) {
        req.validateTLSCertificates(value);
        return this;
    }

    @Override
    public Connection data(String key, String value) {
        req.data(HttpKeyVal.create(key, value));
        return this;
    }

    @Override
    public Connection sslSocketFactory(SSLSocketFactory sslSocketFactory) {
        req.sslSocketFactory(sslSocketFactory);
        return this;
    }

    @Override
    public Connection data(String key, String filename, InputStream inputStream) {
        req.data(HttpKeyVal.create(key, filename, inputStream));
        return this;
    }

    @Override
    public Connection data(String key, String filename, InputStream inputStream, IHttp.OnStreamWriteListener listener) {
        req.data(HttpKeyVal.create(key, filename, inputStream, listener));
        return this;
    }

    @Override
    public Connection data(String key, String filename, InputStream inputStream, String contentType) {
        req.data(HttpKeyVal.create(key, filename, inputStream).contentType(contentType));
        return this;
    }

    @Override
    public Connection data(Map<String, String> data) {
        Validate.notNull(data, "Data map must not be null");
        for (Map.Entry<String, String> entry : data.entrySet()) {
            req.data(HttpKeyVal.create(entry.getKey(), entry.getValue()));
        }
        return this;
    }

    @Override
    public Connection data(String... keyvals) {
        Validate.notNull(keyvals, "Data key value pairs must not be null");
        Validate.isTrue(keyvals.length % 2 == 0, "Must supply an even number of key value pairs");
        for (int i = 0; i < keyvals.length; i += 2) {
            String key = keyvals[i];
            String value = keyvals[i + 1];
            Validate.notEmpty(key, "Data key must not be empty");
            Validate.notNull(value, "Data value must not be null");
            req.data(HttpKeyVal.create(key, value));
        }
        return this;
    }

    @Override
    public Connection data(Collection<KeyVal> data) {
        Validate.notNull(data, "Data collection must not be null");
        for (KeyVal entry : data) {
            req.data(entry);
        }
        return this;
    }

    @Override
    public KeyVal data(String key) {
        Validate.notEmpty(key, "Data key must not be empty");
        for (KeyVal keyVal : request().data()) {
            if (keyVal.key().equals(key))
                return keyVal;
        }
        return null;
    }

    @Override
    public Connection requestBody(String body) {
        req.requestBody(body);
        return this;
    }

    @Override
    public Connection header(String name, String value) {
        req.header(name, value);
        return this;
    }

    @Override
    public Connection headers(Map<String, String> headers) {
        Validate.notNull(headers, "Header map must not be null");
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            req.header(entry.getKey(), entry.getValue());
        }
        return this;
    }

    @Override
    public Connection cookie(String cookie) {
        Validate.notNull(cookie, "Cookie must not be null");
        req.header(HttpHeader.COOKIE, cookie);
        return this;
    }

    @Override
    public Connection cookie(String name, String value) {
        req.cookie(name, value);
        return this;
    }

    @Override
    public Connection cookies(Map<String, String> cookies) {
        Validate.notNull(cookies, "Cookie map must not be null");
        for (Map.Entry<String, String> entry : cookies.entrySet()) {
            req.cookie(entry.getKey(), entry.getValue());
        }
        return this;
    }

    @Override
    public Connection parser(Parser parser) {
        req.parser(parser);
        return this;
    }

    @Override
    public Connection postDataCharset(String charset) {
        req.postDataCharset(charset);
        return this;
    }


    //------------------------------------------------------------------------------------------------------

    public Request request() {
        return req;
    }

    public Response response() {
        return res;
    }

    @Override
    public Response syncExecute() throws IOException {
        return onExecute();
    }

    @Override
    public String syncToStr() throws IOException {
        res = onExecute();
        return res.body();
    }

    @Override
    public Document syncToHtml() throws IOException {
        res = onExecute();
        return res.parse();
    }

    @Override
    public JSONObject syncToJsonObject() throws IOException, JSONException {
        res = onExecute();
        return new JSONObject(res.body());
    }

    @Override
    public JSONArray syncToJsonArray() throws IOException, JSONException {
        res = onExecute();
        return new JSONArray(res.body());
    }

    @Override
    public Document syncToXml() throws IOException {
        res = onExecute();
        return res.parse();
    }


    @Override
    public final ObservableTask<Response> execute() {
        Observable<Response> observable = Observable.create(new ObservableOnSubscribe<Response>() {

            @Override
            public void subscribe(ObservableEmitter<Response> emitter) throws Exception {
                res = onExecute();
                emitter.onNext(res);
                emitter.onComplete();
            }
        });
        return new ObservableTask<>(observable);
    }

    @Override
    public final ObservableTask<String> toStr() {
        Observable<String> observable = Observable.create(new ObservableOnSubscribe<String>() {

            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                res = onExecute();
                emitter.onNext(res.body());
                emitter.onComplete();
            }
        });
        return new ObservableTask<>(observable);
    }

    @Override
    public final ObservableTask<Document> toHtml() {
        Observable<Document> observable = Observable.create(new ObservableOnSubscribe<Document>() {

            @Override
            public void subscribe(ObservableEmitter<Document> emitter) throws Exception {
                res = onExecute();
                Document doc = res.parse();
                emitter.onNext(doc);
                emitter.onComplete();
            }
        });
        return new ObservableTask<>(observable);
    }

    @Override
    public final ObservableTask<JSONObject> toJsonObject() {
        Observable<JSONObject> observable = Observable.create(new ObservableOnSubscribe<JSONObject>() {

            @Override
            public void subscribe(ObservableEmitter<JSONObject> emitter) throws Exception {
                res = onExecute();
                JSONObject jsonArray = new JSONObject(res.body());
                emitter.onNext(jsonArray);
                emitter.onComplete();
            }
        });
        return new ObservableTask<>(observable);
    }

    @Override
    public final ObservableTask<JSONArray> toJsonArray() {
        Observable<JSONArray> observable = Observable.create(new ObservableOnSubscribe<JSONArray>() {

            @Override
            public void subscribe(ObservableEmitter<JSONArray> emitter) throws Exception {
                res = onExecute();
                JSONArray jsonArray = new JSONArray(res.body());
                emitter.onNext(jsonArray);
                emitter.onComplete();
            }
        });
        return new ObservableTask<>(observable);
    }

    @Override
    public final ObservableTask<Document> toXml() {
        Observable<Document> observable = Observable.create(new ObservableOnSubscribe<Document>() {

            @Override
            public void subscribe(ObservableEmitter<Document> emitter) throws Exception {
                res = onExecute();
                Document doc = res.parse();
                emitter.onNext(doc);
                emitter.onComplete();
            }
        });
        return new ObservableTask<>(observable);
    }


    //-----------------------------------------------listeners-------------------------------------------------

    @Override
    public final Connection onRedirect(IHttp.OnRedirectListener listener) {
//        this.onRedirectListener = listener;
        req.onRedirect(listener);
        return this;
    }

//    @Override
//    public Connection onSubscribe(IHttp.OnSubscribeListener listener) {
//        this.onSubscribeListener = listener;
//        return this;
//    }
//
//    @Override
//    public final Connection onError(IHttp.OnErrorListener listener) {
//        this.onErrorListener = listener;
//        return this;
//    }
//
//    @Override
//    public final <T> Connection onSuccess(IHttp.OnSuccessListener<T> listener) {
//        this.onSuccessListener = listener;
//        return this;
//    }
//
//    @Override
//    public Connection onComplete(IHttp.OnCompleteListener listener) {
//        this.onCompleteListener = listener;
//        return this;
//    }

    //----------------------------------------------------headers---------------------------------------------------

    @Override
    public Connection userAgent(String userAgent) {
        Validate.notNull(userAgent, "User-Agent must not be null");
        req.header(HttpHeader.USER_AGENT, userAgent);
        return this;
    }

    @Override
    public Connection referer(String referer) {
        Validate.notNull(referer, "Referrer must not be null");
        req.header(HttpHeader.REFERER, referer);
        return this;
    }

    @Override
    public Connection contentType(String contentType) {
        Validate.notNull(contentType, "Content-Type must not be null");
        req.header(HttpHeader.CONTENT_TYPE, contentType);
        return this;
    }

    //-----------------------------------------------------abstract methods-----------------------------------------------------
    public abstract Request createRequest();

    //    public abstract Response createResponse();
    public abstract Response onExecute() throws IOException;

    //-------------------------------------------------static methods---------------------------------------------------------

}
