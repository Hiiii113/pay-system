package hiiii113.util;

import com.alibaba.fastjson.JSON;

import java.io.PrintWriter;

public class JsonUtil
{
    /**
     * 统一发送 JSON 响应的辅助方法
     *
     * @param writer 输出流
     * @param code   业务状态码
     * @param msg    提示信息
     * @param data   数据负载
     */
    public static void sendJson(PrintWriter writer, int code, String msg, Object data)
    {
        if (writer == null) return;

        String dataStr = (data == null) ? "null" : JSON.toJSONString(data);

        StringBuilder json = new StringBuilder();
        json.append("{")
                .append("\"code\":").append(code).append(",")
                .append("\"msg\":\"").append(msg.replace("\"", "\\\"")).append("\",")
                .append("\"data\":").append(dataStr)
                .append("}");

        writer.write(json.toString());
        writer.flush();
    }
}
