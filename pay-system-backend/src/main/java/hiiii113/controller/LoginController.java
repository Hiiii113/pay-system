package hiiii113.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hiiii113.entity.User;
import hiiii113.exception.BusinessException;
import hiiii113.service.UserService;
import hiiii113.service.impl.UserServiceImpl;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;

import static hiiii113.util.JsonUtil.sendJson;

/**
 * 登录控制器
 * 处理 /login 请求
 */
@WebServlet("/login")
public class LoginController extends HttpServlet
{
    // 实例化Service层
    private final UserService userService = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        // 安全规范：不支持GET请求登录
        response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        // 返回JSON数据
        PrintWriter writer = response.getWriter();
        response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        sendJson(writer, 405, "登录方法不能使用GET请求", null);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    {
        // 设置编码和响应头
        try
        {
            request.setCharacterEncoding("UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            throw new RuntimeException(e);
        }
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        PrintWriter writer = null;
        try
        {
            // 获取输出流
            writer = response.getWriter();

            // 获取参数
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(request.getInputStream());
            // 提取参数
            JsonNode usernameNode = root.get("username");
            JsonNode passwordNode = root.get("password");

            // 校验
            if (usernameNode == null || passwordNode == null)
            {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendJson(writer, 400, "用户名和密码不能为空", null);
                return;
            }

            String username = usernameNode.asText();
            String password = passwordNode.asText();

            // 调用Service层的登录函数
            boolean isSuccess = userService.Login(username, password);

            // 获取结果并返回
            if (isSuccess)
            {
                // 登录成功
                // 通过用户名获取用户对象并返回
                User user = userService.getUserByUsername(username);
                response.setStatus(HttpServletResponse.SC_OK);
                sendJson(writer, 200, "登录成功", user);
            }
            else
            {
                // 登录失败
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 设置状态码401
                sendJson(writer, 401, "用户名或密码错误", null);
            }

        }
        catch (BusinessException e)
        {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            sendJson(writer, 400, e.getMessage(), null);
        }
        catch (SQLException e) // 数据库异常
        {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            sendJson(writer, 500, "数据库连接异常", null);
        }
        catch (Exception e) // 其他异常
        {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            sendJson(writer, 500, "服务器内部错误", null);
        }
    }
}