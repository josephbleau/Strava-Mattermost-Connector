package com.josephbleau.stravamattermostconnector.service.registration;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@Scope(value="session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class VerificationCodeManager {

    private final int length;
    private final char[] characters;
    private final String code;

    public VerificationCodeManager() {
        this.length = 4;
        this.characters = new char[]{'S','T','R','V','A', '1', '2','3','4','5'};
        this.code = generateCode();
    }

    protected VerificationCodeManager(final int length, final char[] characters, final String code) {
        this.length = length;
        this.characters = characters;
        this.code = code;
    }

    /**
     * Generate a random four digit code.
     */
    protected String generateCode() {
        StringBuilder code = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; ++i) {
            code.append(characters[random.nextInt(characters.length)]);
        }

        return code.toString();
    }

    public boolean verify(String codeToTest) {
        return this.code.equals(codeToTest);
    }

    public String getCode() {
        return this.code;
    }

}
