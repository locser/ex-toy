1. User click "Login with Google" -> redirect tới Google consent screen
2. User chọn account và đồng ý cho phép -> Google trả về authorization code
3. Backend dùng code này để lấy access token từ Google
4. Backend dùng access token để lấy thông tin user từ Google API
5. Backend tạo JWT token và trả về cho frontend
