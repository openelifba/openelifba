# Security Policy

## Supported Versions

| Version | Supported          |
| ------- | ------------------ |
| latest  | :white_check_mark: |

## Reporting a Vulnerability

We take security vulnerabilities seriously. If you discover a security issue, please report it responsibly.

### How to Report

1. **Do NOT open a public GitHub issue** for security vulnerabilities
2. Instead, please report security issues via [GitHub Security Advisories](https://github.com/wordiam/openelifba/security/advisories/new)
3. Alternatively, you can email the maintainers directly (if contact is available)

### What to Include

When reporting a vulnerability, please include:

- Description of the vulnerability
- Steps to reproduce the issue
- Potential impact of the vulnerability
- Any suggested fixes (if you have them)

### Response Timeline

- We will acknowledge receipt of your report within 48 hours
- We will provide a detailed response within 7 days
- We will work on a fix and coordinate disclosure with you

### Disclosure Policy

- We follow responsible disclosure practices
- We will credit reporters in security advisories (unless they prefer to remain anonymous)
- We ask that you give us reasonable time to address the issue before public disclosure

## Security Best Practices for Contributors

When contributing to this project, please:

1. **Never commit secrets** - Use environment variables for sensitive configuration
2. **Keep dependencies updated** - Check for security advisories in dependencies
3. **Follow secure coding practices** - Validate input, use parameterized queries, etc.
4. **Review code for security issues** - Look for common vulnerabilities (OWASP Top 10)

## Dependencies

This project uses Dependabot to monitor and update dependencies for security vulnerabilities.
