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


@WebServlet("/transfer")
public class TransferController extends HttpServlet
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
        sendJson(writer, 405, "转账方法不能使用GET请求", null);
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
            JsonNode userIdNode = root.get("userId");
            JsonNode targetUserIdNode = root.get("targetUserId");
            JsonNode amountNode = root.get("amount");

            // 校验
            if (userIdNode == null || targetUserIdNode == null || amountNode == null)
            {
                sendJson(writer, 400, "金额和用户id不能为空", null);
                return;
            }

            // 转换参数
            int userId = userIdNode.asInt();
            int targetUserId = targetUserIdNode.asInt();
            BigDecimal amount = new BigDecimal(amountNode.asText());

            // 开始执行转账方法
            transactionService.transfer(userId, targetUserId, amount);

            // 查看当前余额并返回
            User user = userService.getUserById(userId);
            BigDecimal balance = user.getBalance();

            // 返回转账成功的提示
            sendJson(writer, 200, "转账成功", balance);
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
