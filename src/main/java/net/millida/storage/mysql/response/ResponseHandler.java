package net.millida.storage.mysql.response;

public interface ResponseHandler<R, O, T extends Throwable> {

    R handleResponse(O o) throws T;
}
