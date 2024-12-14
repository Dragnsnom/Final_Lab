package com.example.userservice.app.aspect;

import com.example.userservice.web.dto.requests.EmailAndPassportDto;
import com.example.userservice.web.dto.responses.UserInfoDto;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * LoggerAspect aspect responsible for logging the results of the methods
 */
@Component
@Aspect
@Slf4j
public class LoggerAspect {

    /**
     * Advice for logging the start of a method action in the controller class {@code UserController}
     * @param joinPoint the {@code JoinPoint} an object that allows you to access information about the method
     */
    @Before("execution (* findPassportAndEmail(..))")
    public void loggingController(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        EmailAndPassportDto dto = (EmailAndPassportDto) args[0];
        log.info("Checking the existence, of an email, a passport number in the database: email = {} " +
                        "and passport number = {}", dto.getEmail(), dto.getPassportNumber());
    }

    /**
     * Advice for logging the result of executing a method in the service class {@code ContactService}
     * @param response the {@code Boolean} result of the method for checking the passport number or email in the database
     */
    @AfterReturning(pointcut = "execution (* isExistPassportNumberAndEmail(..))", returning = "response")
    public void loggerService(Boolean response) {
        log.info("The result of checking the passport number or email address is in the database: {}", response);
    }

    /**
     * Advice for logging the start of a method {@code getInfoByUser(UUID clientId)} action in the controller class
     * {@code UserController}
     *
     * @param joinPoint the {@code JoinPoint} an object that allows you to access information about the method
     */
    @Before("execution (* getInfoByUser(..))")
    public void loggingGetInfoByUser(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        UUID clientId = (UUID) args[0];
        log.info("Search for information about the bank's customer by identification number: {}", clientId);
    }

    /**
     * Advice for logging the result of executing a method {@code getUserInfoById(UUID clientId)} in the service class
     * {@code ClientService}
     *
     * @param response the {@code UserInfoDto} information about the bank's client
     */
    @AfterReturning(pointcut = "execution (* getUserInfoById(..))", returning = "response")
    public void loggerGetUserInfoById(UserInfoDto response) {
        log.info("The result of receiving information about the bank's client: {}", response);
    }

    /**
     * AOP advice method to log the initiation of setting a new security question
     * and answer for a client in the UserController's {@code updateSecurityQuestion} method.
     *
     * @param joinPoint The join point at which the advice is applied
     */
    @Before("execution (* com.example.userservice.web.controller.UserController.updateSecurityQuestion(..))")
    public void beforeUpdateSecurityQuestion(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        UUID clientId = (UUID) args[0];

        log.info("Setting new security question and answer for client = {}", clientId);
    }

    /**
     * AOP advice method to log the successful setting of a new security question
     * and answer for a client in the UserProfileServiceImpl's {@code updateSecurityQuestion} method.
     *
     * @param joinPoint The join point at which the advice is applied
     */
    @After("execution (* com.example.userservice.app.service.impl.UserProfileServiceImpl.updateSecurityQuestion(..))")
    public void afterUpdateSecurityQuestion(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        UUID id = (UUID) args[0];

        log.info("New security question and answer for client with ID = {} were set", id);
    }

    /**
     * AOP advice method to log the exception when failing to set a new security question
     * and answer for a client in the UserProfileServiceImpl's {@code updateSecurityQuestion} method.
     *
     * @param joinPoint The join point at which the advice is applied
     */
    @AfterThrowing(pointcut = "execution (* com.example.userservice.app.service.impl.UserProfileServiceImpl.updateSecurityQuestion(..))")
    public void afterUpdateSecurityQuestionException(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        UUID id = (UUID) args[0];

        log.error("Failed to set new security question and answer for client with ID = {}", id);
    }

    /**
     * AOP advice method to log the verification of the provided old password
     * for a client in the UserController's {@code updatedPassword} method.
     *
     * @param joinPoint The join point at which the advice is applied
     */
    @After("execution (* com.example.userservice.web.controller.UserController.updatedPassword(..))")
    public void afterUpdatedPasswordCheck(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        UUID clientId = (UUID) args[0];

        log.info("Checking if the provided old password is correct for client = {}", clientId);
    }

    /**
     * AOP advice method to log the successful setting of a new password
     * for a client in the UserController's {@code updatedPassword} method.
     *
     * @param joinPoint The join point at which the advice is applied
     */
    @AfterReturning("execution (* com.example.userservice.web.controller.UserController.updatedPassword(..))")
    public void afterUpdatedPasswordSuccess(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        UUID clientId = (UUID) args[0];

        log.info("Setting new password for client = {}", clientId);
    }

    /**
     * AOP advice method to log the exception when failing to set a new password
     * for a client in the UserController's {@code updatedPassword} method.
     *
     * @param joinPoint The join point at which the advice is applied
     */
    @AfterThrowing(pointcut = "execution (* com.example.userservice.web.controller.UserController.updatedPassword(..))")
    public void afterUpdatedPasswordException(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        UUID clientId = (UUID) args[0];

        log.info("Failed to set new password for client = {}", clientId);
    }

    /**
     * AOP advice method to log the successful setting of a new password
     * for a client in the UserProfileServiceImpl's {@code updatePassword} method.
     *
     * @param joinPoint The join point at which the advice is applied
     */
    @After("execution (* com.example.userservice.app.service.impl.UserProfileServiceImpl.updatePassword(..))")
    public void afterUpdatePasswordSuccess(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        UUID clientId = (UUID) args[0];

        log.info("New password for client = {} was set", clientId);
    }
}
