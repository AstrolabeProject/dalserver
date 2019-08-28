package ca.nrc.cadc.auth;

import javax.security.auth.*;

/**
 * Subject modification implementation class.
 *
 * @author Tom Hicks
 */
public class AuthenticatorImpl implements Authenticator {
    /**
     * Modify a Subject and return it. Implementations can modify
     * the specified Subject by adding identities (Principals) or credentials.
     * The argument subject will typically have an HttpPrinncipal if the request
     * went through HTTP authentication, or an X500principal if the user
     * authenticated via SSL with client certficate. The typical usage is to add
     * additional principals with internal identity information. This could then
     * be used in an Authorizer implementation elsewhere in the application.
     *
     * @param subject the initial subject
     * @return the modified subject
     */
  public Subject getSubject (Subject subject) {
    return subject;                         // initially NOOP
  }

}
