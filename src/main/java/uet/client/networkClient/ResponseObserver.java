package uet.client.networkClient;

import uet.common.payLoad.Response;

public interface ResponseObserver {
    void onResponse(Response response);
}
