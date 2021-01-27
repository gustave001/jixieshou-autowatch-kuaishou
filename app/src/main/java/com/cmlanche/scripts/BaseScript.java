package com.cmlanche.scripts;

import com.cmlanche.application.MyApplication;
import com.cmlanche.common.PackageUtils;
import com.cmlanche.core.search.FindById;
import com.cmlanche.core.search.FindByText;
import com.cmlanche.core.search.node.NodeInfo;
import com.cmlanche.core.utils.Logger;
import com.cmlanche.core.utils.Utils;
import com.cmlanche.model.AppInfo;

import java.util.Random;

public abstract class BaseScript implements IScript {

    private AppInfo appInfo;
    private long startTime;

    public BaseScript(AppInfo appInfo) {
        this.appInfo = appInfo;
    }

    protected long getTimeout() {
        return appInfo.getPeriod() * 60 * 60 * 1000;
    }

    @Override
    public void execute() {
        startApp();
        resetStartTime();

        // 总时间
        while ((System.currentTimeMillis() - startTime < getTimeout())) {
            try {
                if (isPause()) {
                    Utils.sleep(2000);
                    continue;
                }
                executeScript();
            } catch (Exception e) {
                Logger.e("执行异常，脚本: " + appInfo.getName(), e);
            } finally {
                int t = getRandomSleepTime(appInfo.getSleepTime() - 5 < 0 ? 0 : appInfo.getSleepTime(), appInfo.getSleepTime() + 5);
                Logger.i("休眠：" + t);
                Utils.sleep(t);
            }
        }
    }

    private boolean isPause() {
        return TaskExecutor.getInstance().isForcePause() ||
                TaskExecutor.getInstance().isPause();
    }

    @Override
    public AppInfo getAppInfo() {
        return appInfo;
    }

    @Override
    public void startApp() {
        PackageUtils.startApp(getAppInfo().getPkgName());
    }

    @Override
    public void resetStartTime() {
        this.startTime = System.currentTimeMillis();
    }

    /**
     * 获取一个随机的休眠时间
     *
     * @param min
     * @param max
     * @return
     */
    private int getRandomSleepTime(int min, int max) {
        Random random = new Random();
        return (random.nextInt(max) % (max - min + 1) + min) * 1000;
    }

    /**
     * 通过id来查找控件
     *
     * @param id
     * @return
     */
    protected NodeInfo findById(String id) {
        return FindById.find(id);
    }

    protected NodeInfo findByText(String text) {
        return FindByText.find(text);
    }

    protected void runOnUiThread(Runnable runnable) {
        MyApplication.getAppInstance().getMainActivity().runOnUiThread(runnable);
    }

    /**
     * 当前页面是否是目标页面
     * @return
     */
    protected boolean isTargetPkg() {
        if(MyApplication.getAppInstance().getAccessbilityService().isWrokFine()) {
            if(!MyApplication.getAppInstance().getAccessbilityService().containsPkg(getAppInfo().getPkgName())) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取最大休眠时间
     *
     * @return
     */
    protected abstract int getMaxSleepTime();

    /**
     * 获取最小休眠时间
     *
     * @return
     */
    protected abstract int getMinSleepTime();

    /**
     * 执行脚本
     */
    protected abstract void executeScript();
}
