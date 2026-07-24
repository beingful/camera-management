# Coding Rules

## Java Naming Conventions

- Use `PascalCase` for class, interface, enum, annotation, and record names.
- Use `camelCase` for method names, variable names, parameters, and non-constant fields.
- Use `UPPER_SNAKE_CASE` for constants declared as `static final`.
- Use lowercase package names, separated by dots, such as `com.example.project`.
- Use lowercase folder names for Java package directories, matching the package structure exactly.
- Name Java source files after the public top-level type they contain, such as `UserService.java` for `public class UserService`.
- Use descriptive names that reveal intent; avoid abbreviations unless they are widely understood in the project domain.
- Use nouns or noun phrases for classes and records, such as `PaymentRequest`.
- Use verbs or verb phrases for methods, such as `calculateTotal` or `sendNotification`.
- Use plural names for collections when they contain multiple values, such as `users` or `orderItems`.
- Use conventional interface names without an `I` prefix unless the existing project style requires it.
- Use test class names that clearly identify the unit under test, such as `UserServiceTest`.

## Best Coding Practices

- Prefer simple, readable code over clever or overly abstract solutions.
- Follow the existing project structure, style, and framework conventions before introducing new patterns.
- Keep classes and methods focused on a single responsibility.
- Keep methods short enough to understand without excessive scrolling.
- Avoid duplicated logic; extract shared behavior when it improves clarity.
- Validate inputs at module boundaries and fail with clear exceptions or error responses.
- Prefer immutability where practical, especially for value objects and method parameters.
- Encapsulate mutable state and avoid exposing internal collections directly.
- Use dependency injection instead of creating hard-coded dependencies inside business logic.
- Avoid global mutable state and hidden side effects.
- Handle exceptions deliberately; do not swallow exceptions silently.
- Use meaningful logging at service and integration boundaries without logging secrets or sensitive data.
- Prefer standard library and established project utilities over custom implementations.
- Write unit tests for business logic and regression tests for bug fixes.
- Keep tests deterministic, isolated, and named after the behavior they verify.
- Avoid magic numbers and unexplained literals; introduce named constants when the meaning is not obvious.
- Keep comments concise and useful; explain why something exists when the code cannot make that clear.
- Do not introduce unrelated formatting, refactoring, or dependency changes while solving a focused task.
- Ensure generated code compiles and follows these rules unless an existing project convention explicitly differs.
