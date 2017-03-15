package com.prize.facialrecognize.result;

/**
 * Created by Administrator on 2017/3/15.
 */

public class ErrorResult {

    /**
     * error_code : 216402
     * error_msg : face not found
     * log_id : 476662320
     */

    private int error_code;
    private String error_msg;
    private long log_id;

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public String getError_msg() {
        return error_msg;
    }

    public void setError_msg(String error_msg) {
        this.error_msg = error_msg;
    }

    public long getLog_id() {
        return log_id;
    }

    public void setLog_id(int log_id) {
        this.log_id = log_id;
    }

    @Override
    public String toString() {
        return "ErrorResult{" +
                "error_code=" + error_code +
                ", error_msg='" + error_msg + '\'' +
                ", log_id=" + log_id +
                '}';
    }
}
