package com.youtube_project.contollers;

import com.youtube_project.model.exceptions.UnauthorizedException;
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
        String sessionIp = (String) session.getAttribute(REMOTE_IP);
        String requestIp = request.getRemoteAddr();
        boolean newSession = session.isNew();
        boolean logged = session.getAttribute(LOGGED) != null && ((Boolean) session.getAttribute(LOGGED));
        boolean userIdIsNull = session.getAttribute(USER_ID) == null;
        boolean ipCheck = requestIp.equals(sessionIp);
        if (userIdIsNull || newSession || !logged) {
            throw new UnauthorizedException("You have to log in");
        }
        // check if request is from the same ip logged in the session
        if(!ipCheck){
            session.invalidate();
            throw new UnauthorizedException("Dont steal sessions!");
        }

    }

    public Long getSessionUserId(HttpServletRequest request) {

        long loggedUserId;
        if (request.getSession().getAttribute(USER_ID) == null){
            loggedUserId = 0;
        }else {
            loggedUserId = (long)request.getSession().getAttribute(USER_ID);
        }
        return loggedUserId;
    }

    public void buildSessionInfo(HttpServletRequest request, long userId) {
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
