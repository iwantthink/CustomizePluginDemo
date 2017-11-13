package design.command;

/**
 * Created by renbo on 2017/11/13.
 */

public class Invoker {
    //持有一个对相应命令对象的引用
    private Command mCommand;

    public Invoker(Command command) {
        mCommand = command;
    }

    public void action() {
        //调用具体命令对象的相关方法,执行具体命令
        mCommand.execute();
    }
}
