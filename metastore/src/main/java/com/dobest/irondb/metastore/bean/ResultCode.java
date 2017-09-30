package com.dobest.irondb.metastore.bean;

/**
 * Created by Micheal on 2017/9/23.
 */

/**
 * 一些异常项 暂未设计 完成
 */
public class ResultCode {

    private String status;
    private Object message;
    private int code;

    public void ResultCodeSeting(ErrorType error, Exception e) {
        switch (error) {
            case TableExits:
                this.status = "table is exits  please change table Name";
                this.code = error.getValue();
                this.message = e;
            case Success:
                this.status = " operation success ";
                this.code = error.getValue();
                this.message = null;
            case InsertError:
                this.status = "Insert operation ";
                this.code = error.getValue();
                this.message = e;
            case UpdateError:
                this.status = "update operation ";
                this.code = error.getValue();
                this.message = e;
            default:
                this.status = " Unknown operation ";
                this.code = -1;
                this.message = null;
        }
    }
}