package hiiii113.util;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class JsonUtil
{
    private static final ObjectMapper mapper = new ObjectMapper();

    static
    {
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

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
        Map<String, Object> result = new HashMap<>();
        result.put("code", code);
        result.put("msg", msg);
        result.put("data", data);

        try
        {
            String jsonStr = mapper.writeValueAsString(result);

            writer.print(jsonStr);
            writer.flush();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            writer.print("{\"code\":500,\"msg\":\"服务器JSON转换异常\",\"data\":null}");
        }
    }
}
