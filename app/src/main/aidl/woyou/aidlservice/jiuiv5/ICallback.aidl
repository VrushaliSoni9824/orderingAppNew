package woyou.aidlservice.jiuiv5;


interface ICallback {


	oneway void onRunResult(in boolean isSuccess, int code, String msg);

}