package hiiii113.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hiiii113.entity.User;
import hiiii113.exception.BusinessException;
import hiiii113.service.TransactionService;
import hiiii113.service.UserService;
import hiiii113.service.impl.TransactionServiceImpl;
import hiiii113.service.impl.UserServiceImpl;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.SQLException;

import static hiiii113.util.JsonUtil.sendJson;

@WebServlet("/deposit")
public class DepositController extends HttpServlet
{
    // 实例化Service层
    private final TransactionService transactionService = new TransactionServiceImpl();
    private final UserService userService = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        // 安全规范：不支持GET请求登录
        response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        // 返回JSON数据
        PrintWriter writer = response.getWriter();
        response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        sendJson(writer, 405, "存款方法不能使用GET请求", null);
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

            // 接收参数
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(request.getInputStream());
            JsonNode userIdNode = root.get("userId");
            JsonNode amountNode = root.get("amount");

            // 校验
            if (userIdNode == null || amountNode == null)
            {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                sendJson(writer, 400, "金额和用户id不能为空", null);
                return;
            }

            // 转化参数
            int userId = userIdNode.asInt();
            BigDecimal amount = new BigDecimal(amountNode.asText());

            // 执行存款操作
            transactionService.deposit(userId, amount);

            // 查看当前余额并返回
            User user = userService.getUserById(userId);
            BigDecimal balance = user.getBalance();

            // 返回存款成功的提示
            response.setStatus(HttpServletResponse.SC_OK);
            sendJson(writer, 200, "存款成功", balance);
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
