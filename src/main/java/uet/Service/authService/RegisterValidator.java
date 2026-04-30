package uet.Service.authService;
// class để kiểm tra định dạng mật khẩu, email khi đăng ký
public class RegisterValidator {
    public boolean validateEmailFormat(String email){
        String regex = "^[a-zA-Z0-9._%+-]+@gmail\\.com$";  // $ để kết thúc chuỗi
    // ^ : để bắt đầu chuỗi , [a-zA-Z0-9._%+-]+ : phần tên email cho phép chữ, số ; @gmail\\.com : phải đúng @gmail.com
        return email.matches(regex);
    }
    public boolean validatePasswordFormat(String password){
        return password.length() >= 8 && password.matches(".*[a-z].*") && password.matches(".*[0-9].*");
    }
}
