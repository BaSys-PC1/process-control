package de.dfki.cos.basys.processcontrol.cctaskmanager.services;

import de.dfki.cos.basys.processcontrol.model.ControlComponentResponse;

public interface ControlComponentAgentCallback {


    void onControlComponentResponse(ControlComponentResponse response);

}
