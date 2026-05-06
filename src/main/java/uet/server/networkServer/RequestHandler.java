package uet.server.networkServer;

import uet.common.payLoad.Request;
import uet.common.payLoad.Response;

public interface RequestHandler {
    Response handle(Request request);
}
