package locser.config;

// @Configuration
// @EnableWebSecurity
// @EnableMethodSecurity // Bật bảo mật phương thức
public class SecurityConfig {

  // @Autowired
  // private UserRepository userRepository;

  // SecurityConfig() {
  // System.out.println("=== SecurityConfig: Constructor called ===");
  // }

  // @Bean
  // public SecurityFilterChain filterChain(HttpSecurity http,
  // OAuth2UserService<OAuth2UserRequest, OAuth2User> customOAuth2UserService)
  // throws Exception {
  // http
  // .csrf(csrf -> csrf.disable())
  // .authorizeHttpRequests(authz -> authz
  // .requestMatchers("/error", "/webjars/**", "/actuator/**")
  // .permitAll()
  // .anyRequest().authenticated())
  // .oauth2Login(oauth2 -> oauth2
  // .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
  // .defaultSuccessUrl("/login/google", true)
  // .failureUrl("/login/error?error=true"))
  // .logout(logout -> logout
  // .logoutSuccessUrl("/").permitAll());
  // return http.build();
  // }

  // @Bean("customOAuth2UserService")
  // @Primary
  // public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {
  // System.out.println("=== SecurityConfig: Creating oauth2UserService bean
  // ===");
  // DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
  // return request -> {
  // System.out.println("=== SecurityConfig: oauth2UserService.loadUser() called
  // ===");
  // System.out.println("=== SecurityConfig: Client Registration ID: "
  // + request.getClientRegistration().getRegistrationId() + " ===");

  // OAuth2User oauth2User = delegate.loadUser(request);

  // // TODO: Custom Logic Here
  // // 1. Get registrationId (google, github, etc.)
  // String registrationId = request.getClientRegistration().getRegistrationId();
  // // 2. Get user attributes
  // java.util.Map<String, Object> attributes = oauth2User.getAttributes();
  // // 3. Determine unique identifier (e.g., email, id from provider)
  // String userIdentifier = null;
  // if ("google".equals(registrationId)) {
  // userIdentifier = (String) attributes.get("email"); // Google uses email
  // } else if ("github".equals(registrationId)) {
  // userIdentifier = ((Integer) attributes.get("id")).toString(); // GitHub uses
  // id
  // // Or you might use 'login' for username, or 'email' if scope user:email is
  // // granted
  // }
  // // Add logic for other providers

  // if (userIdentifier == null) {
  // throw new OAuth2AuthenticationException(
  // "Cannot determine user identifier from OAuth2 provider: " + registrationId);
  // }

  // // 4. Check if user exists in your database
  // // UserEntity user =
  // userRepository.findByProviderAndProviderId(registrationId,
  // // userIdentifier);
  // User user = this.userRepository.findByEmail(userIdentifier); // If using
  // email as primary key

  // System.out.println("SecurityConfig user: " + user);
  // // 5. If user does not exist, create new user record
  // if (user == null) {
  // user = new User();
  // // user.setProvider(registrationId);
  // // user.setProviderId(userIdentifier); // Or set email, etc.
  // user.setEmail(userIdentifier);
  // user.setName((String) attributes.get("name")); // Assuming 'name' exists
  // userRepository.save(user);
  // user.setStatus(1);
  // user.setProvider((registrationId));
  // }

  // // 6. Update user info if necessary (e.g., update name, avatar_url)
  // // user.setLastLogin(LocalDateTime.now());
  // // userRepository.save(user);

  // // 7. Define authorities/roles for the user
  // Set authorities = new HashSet<>();
  // authorities.addAll(oauth2User.getAuthorities()); // Keep default authorities
  // from provider
  // authorities.add(new SimpleGrantedAuthority("ROLE_USER")); // Add a default
  // role
  // // If you have roles in your database:
  // // user.getRoles().forEach(role -> authorities.add(new
  // // SimpleGrantedAuthority("ROLE_" + role.getName())));

  // // 8. Return a new OAuth2User or a custom implementation
  // // We wrap the original user with potentially updated authorities and a new
  // name
  // // attribute key if needed
  // String userNameAttributeName =
  // request.getClientRegistration().getProviderDetails()
  // .getUserInfoEndpoint().getUserNameAttributeName();
  // if (userNameAttributeName == null) {
  // // Fallback to a common attribute name if provider doesn't specify one
  // userNameAttributeName = "name"; // Or "login" for GitHub
  // }

  // System.out.println(
  // "SecurityConfig DefaultOAuth2User DefaultOAuth2User : " + new
  // DefaultOAuth2User(
  // authorities,
  // attributes, // Use original attributes or modify them
  // userNameAttributeName // Key in attributes that represents the user's
  // name/identifier
  // ));

  // // Note: DefaultOAuth2User constructor takes authorities, attributes, and
  // // nameAttributeKey
  // return new DefaultOAuth2User(
  // authorities,
  // attributes, // Use original attributes or modify them
  // userNameAttributeName // Key in attributes that represents the user's
  // name/identifier
  // );
  // };
  // }
}