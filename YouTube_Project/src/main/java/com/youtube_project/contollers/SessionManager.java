package com.youtube_project.contollers;

import com.youtube_project.models.exceptions.UnauthorizedException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Component
public class SessionManager {

    public static final String LOGGED = "logged";
    public static final String USER_ID = "user_id";
    public static final String REMOTE_IP = "user_ip";

    public void validateLogin(HttpServletRequest request) {
        HttpSession session = request.getSession();
        boolean newSession = session.isNew();
        boolean logged = session.getAttribute(LOGGED) != null && ((Boolean) session.getAttribute(LOGGED));
        boolean userIdIsNull = session.getAttribute(USER_ID) == null;
        if (userIdIsNull || newSession || !logged) {
            throw new UnauthorizedException("You have to log in");
        }

    }

    public Long getSessionUserId(HttpServletRequest request) {
        HttpSession session = request.getSession();
        String ip = request.getRemoteAddr();
        if (session.getAttribute(USER_ID) == null ||
                        session.isNew() ||
                        !session.getAttribute(REMOTE_IP).equals(ip)) {
            throw new UnauthorizedException("You have to log in");
        }
        return (long) session.getAttribute(USER_ID);
    }

    public void setSession(HttpServletRequest request, long userId) {
        HttpSession session = request.getSession();
        session.setAttribute(LOGGED, true);
        session.setAttribute(USER_ID, userId);
        session.setAttribute(REMOTE_IP, request.getRemoteAddr());
        session.setMaxInactiveInterval(1800);
    }

    public boolean isUserLogged(HttpServletRequest request) {
        return request.getSession().getAttribute(LOGGED) != null;
    }

}
