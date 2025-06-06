package com.xxl.job.admin.core.alarm.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.xxl.job.admin.core.alarm.JobAlarm;
import com.xxl.job.admin.core.conf.XxlJobAdminConfig;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.model.XxlJobLog;
import com.xxl.job.admin.core.util.I18nUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * job alarm by WeChat enterprise
 *
 * @author tie yan
 */
@Component
public class WeChatEnterpriseJobAlarm implements JobAlarm {
    private static final Logger logger = LoggerFactory.getLogger(WeChatEnterpriseJobAlarm.class);

    @Override
    public boolean doAlarm(XxlJobInfo info, XxlJobLog jobLog) {
        if (info != null && info.getAlarmWeChatWebhook() != null && !info.getAlarmWeChatWebhook().trim().isEmpty()) {
            try {
                XxlJobGroup group = XxlJobAdminConfig.getAdminConfig().getXxlJobGroupDao().load(info.getJobGroup());
                String text = "####" + I18nUtil.getString("jobconf_monitor_detail") + "\n" +
                              ">" + I18nUtil.getString("jobinfo_field_jobgroup") + "\n" +
                              (null == group ? "无" : group.getTitle()) + "\n" +
                              ">" + I18nUtil.getString("jobinfo_field_id") + "\n" +
                              info.getId() + "\n" +
                              ">" + I18nUtil.getString("jobinfo_field_jobdesc") + "\n" +
                              info.getJobDesc() + "\n" +
                              ">" + I18nUtil.getString("jobconf_monitor_alarm_title") + "\n" +
                              I18nUtil.getString("jobconf_monitor_alarm_type") + "\n" +
                              ">" + I18nUtil.getString("jobconf_monitor_alarm_content") + "\n" +
                              "日志ID：" + jobLog.getId() + "\n" +
                              "TriggerCode：" + jobLog.getTriggerCode() + "\n" +
                              "TriggerMsg：" + jobLog.getTriggerMsg() + "\n" +
                              "HandleCode：" + jobLog.getHandleCode() + "\n" +
                              "HandleMsg：" + jobLog.getHandleMsg() + "\n";
                JSONObject msg = new JSONObject();
                msg.set("msgtype", "markdown");
                JSONObject content = new JSONObject();
                content.set("content", text);
                msg.set("markdown", content);
                String resultStr = HttpUtil.post(info.getAlarmWeChatWebhook(), JSONUtil.toJsonStr(msg));
                logger.error(">>>>>>>>>>> xxl-job, WeChat Webhook alarm result:{}", resultStr);
            } catch (Exception e) {
                logger.error(">>>>>>>>>>> xxl-job, WeChat Webhook alarm exception:{}, JobLogId:{}", e, jobLog.getId());
            }
        }
        return true;
    }
}
