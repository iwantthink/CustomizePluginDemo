package design.command;

/**
 * Created by renbo on 2017/11/13.
 */

public class ConcreateCommand implements Command {
    //持有一个对接收者对象的引用
    private Receiver mReceiver;

    public ConcreateCommand(Receiver receiver){
        mReceiver = receiver;
    }

    @Override
    public void execute() {
        //调用接收者的相关方法执行具体逻辑
        mReceiver.action();
    }
}
