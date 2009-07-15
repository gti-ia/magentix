#!/bin/bash



cat AcquireRole.owl | sed 's/http:\/\/localhost:8080\/AcquireRole\/services/http:\/\/localhost:8080\/omsservices\/services/' > ../transformado/AcquireRole.owl
cat AcquireRoleProcess.owl | sed 's/http:\/\/localhost:8080\/AcquireRole\/services/http:\/\/localhost:8080\/omsservices\/services/' > ../transformado/AcquireRoleProfile.owl
cat AcquireRoleProfile.owl | sed 's/http:\/\/localhost:8080\/AcquireRole\/services/http:\/\/localhost:8080\/omsservices\/services/' > ../transformado/AcquireRoleProcess.owl


cat DeregisterNorm.owl | sed 's/http:\/\/localhost:8080\/DeregisterNorm\/services/http:\/\/localhost:8080\/omsservices\/services/' > ../transformado/DeregisterNorm.owl
cat DeregisterNormProfile.owl | sed 's/http:\/\/localhost:8080\/DeregisterNorm\/services/http:\/\/localhost:8080\/omsservices\/services/' > ../transformado/DeregisterNormProfile.owl
cat DeregisterNormProcess.owl | sed 's/http:\/\/localhost:8080\/DeregisterNorm\/services/http:\/\/localhost:8080\/omsservices\/services/' > ../transformado/DeregisterNormProcess.owl

cat DeregisterRole.owl | sed 's/http:\/\/localhost:8080\/DeregisterRole\/services/http:\/\/localhost:8080\/omsservices\/services/' > ../transformado/DeregisterRole.owl
cat DeregisterRoleProfile.owl | sed 's/http:\/\/localhost:8080\/DeregisterRole\/services/http:\/\/localhost:8080\/omsservices\/services/' >../transformado/DeregisterRoleProfile.owl
cat DeregisterRoleProcess.owl | sed 's/http:\/\/localhost:8080\/DeregisterRole\/services/http:\/\/localhost:8080\/omsservices\/services/' >../transformado/DeregisterRoleProcess.owl

cat DeregisterUnit.owl | sed 's/http:\/\/localhost:8080\/DeregisterUnit\/services/http:\/\/localhost:8080\/omsservices\/services/' >../transformado/DeregisterUnit.owl
cat DeregisterUnitProcess.owl | sed 's/http:\/\/localhost:8080\/DeregisterUnit\/services/http:\/\/localhost:8080\/omsservices\/services/' > ../transformado/DeregisterUnitProcess.owl
cat DeregisterUnitProfile.owl | sed 's/http:\/\/localhost:8080\/DeregisterUnit\/services/http:\/\/localhost:8080\/omsservices\/services/' > ../transformado/DeregisterUnitProfile.owl

cat Expulse.owl | sed 's/http:\/\/localhost:8080\/Expulse\/services/http:\/\/localhost:8080\/omsservices\/services/' >../transformado/Expulse.owl
cat ExpulseProcess.owl | sed 's/http:\/\/localhost:8080\/Expulse\/services/http:\/\/localhost:8080\/omsservices\/services/' >../transformado/ExpulseProcess.owl
cat ExpulseProfile.owl | sed 's/http:\/\/localhost:8080\/Expulse\/services/http:\/\/localhost:8080\/omsservices\/services/' >../transformado/ExpulseProfile.owl

cat InformAgentRole.owl | sed 's/http:\/\/localhost:8080\/InformAgent\/services/http:\/\/localhost:8080\/omsservices\/services/' >../transformado/InformAgentRole.owl
cat InformAgentRoleProcess.owl | sed 's/http:\/\/localhost:8080\/InformAgent\/services/http:\/\/localhost:8080\/omsservices\/services/' >../transformado/InformAgentRoleProcess.owl
cat InformAgentRoleProfile.owl | sed 's/http:\/\/localhost:8080\/InformAgent\/services/http:\/\/localhost:8080\/omsservices\/services/' >../transformado/InformAgentRoleProfile.owl


cat InformMembers.owl | sed 's/http:\/\/localhost:8080\/InformMembers\/services/http:\/\/localhost:8080\/omsservices\/services/' >../transformado/InformMembers.owl
cat InformMembersProcess.owl | sed 's/http:\/\/localhost:8080\/InformMembers\/services/http:\/\/localhost:8080\/omsservices\/services/' >../transformado/InformMembersProcess.owl
cat InformMembersProfile.owl | sed 's/http:\/\/localhost:8080\/InformMembers\/services/http:\/\/localhost:8080\/omsservices\/services/' >../transformado/InformMembersProfile.owl


cat InformRoleNorms.owl | sed 's/http:\/\/localhost:8080\/InformRoleNorms\/services/http:\/\/localhost:8080\/omsservices\/services/' >../transformado/InformRoleNorms.owl
cat InformRoleNormsProcess.owl | sed 's/http:\/\/localhost:8080\/InformRoleNorms\/services/http:\/\/localhost:8080\/omsservices\/services/' >../transformado/InformRoleNormsProcess.owl
cat InformRoleNormsProfile.owl | sed 's/http:\/\/localhost:8080\/InformRoleNorms\/services/http:\/\/localhost:8080\/omsservices\/services/'>../transformado/InformRoleNormsProfile.owl

cat InformRoleProfiles.owl | sed 's/http:\/\/localhost:8080\/InformRoleProfiles\/services/http:\/\/localhost:8080\/omsservices\/services/' >../transformado/InformRoleProfiles.owl
cat InformRoleProfilesProcess.owl | sed 's/http:\/\/localhost:8080\/InformRoleProfiles\/services/http:\/\/localhost:8080\/omsservices\/services/' >../transformado/InformRoleProfilesProcess.owl
cat InformRoleProfilesProfile.owl | sed 's/http:\/\/localhost:8080\/InformRoleProfiles\/services/http:\/\/localhost:8080\/omsservices\/services/' >../transformado/InformRoleProfilesProfile.owl


cat InformUnit.owl | sed 's/http:\/\/localhost:8080\/InformUnit\/services/http:\/\/localhost:8080\/omsservices\/services/' >../transformado/InformUnit.owl
cat InformUnitProcess.owl | sed 's/http:\/\/localhost:8080\/InformUnit\/services/http:\/\/localhost:8080\/omsservices\/services/' >../transformado/InformUnitProcess.owl
cat InformUnitProfile.owl | sed 's/http:\/\/localhost:8080\/InformUnit\/services/http:\/\/localhost:8080\/omsservices\/services/' >../transformado/InformUnitProfile.owl

cat InformUnitRoles.owl | sed 's/http:\/\/localhost:8080\/InformUnitRoles\/services/http:\/\/localhost:8080\/omsservices\/services/' >../transformado/InformUnit.owl
cat InformUnitRolesProcess.owl | sed 's/http:\/\/localhost:8080\/InformUnitRoles\/services/http:\/\/localhost:8080\/omsservices\/services/' >../transformado/InformUnitProcess.owl
cat InformUnitRolesProfile.owl | sed 's/http:\/\/localhost:8080\/InformUnitRoles\/services/http:\/\/localhost:8080\/omsservices\/services/' >../transformado/InformUnitProfile.owl

cat LeaveRole.owl | sed 's/http:\/\/localhost:8080\/LeaveRole\/services/http:\/\/localhost:8080\/omsservices\/services/' >../transformado/LeaveRole.owl
cat LeaveRoleProcess.owl | sed 's/http:\/\/localhost:8080\/LeaveRole\/services/http:\/\/localhost:8080\/omsservices\/services/'  >../transformado/LeaveRoleProcess.owl
cat LeaveRoleProfile.owl | sed 's/http:\/\/localhost:8080\/LeaveRole\/services/http:\/\/localhost:8080\/omsservices\/services/' >../transformado/LeaveRoleProfile.owl

cat QuantityMembers.owl | sed 's/http:\/\/localhost:8080\/QuantityMembers\/services/http:\/\/localhost:8080\/omsservices\/services/' >../transformado/QuantityMembers.owl
cat QuantityMembersProcess.owl | sed 's/http:\/\/localhost:8080\/QuantityMembers\/services/http:\/\/localhost:8080\/omsservices\/services/'  >../transformado/QuantityMembersProcess.owl
cat QuantityMembersProfile.owl | sed 's/http:\/\/localhost:8080\/QuantityMembers\/services/http:\/\/localhost:8080\/omsservices\/services/' >../transformado/QuantityMembersProfile.owl

cat RegisterRole.owl | sed 's/http:\/\/localhost:8080\/RegisterRole\/services/http:\/\/localhost:8080\/omsservices\/services/' >../transformado/RegisterRole.owl
cat RegisterRoleProcess.owl | sed 's/http:\/\/localhost:8080\/RegisterRole\/services/http:\/\/localhost:8080\/omsservices\/services/'  >../transformado/RegisterRoleProcess.owl
cat RegisterRoleProfile.owl | sed 's/http:\/\/localhost:8080\/RegisterRole\/services/http:\/\/localhost:8080\/omsservices\/services/' >../transformado/RegisterRoleProfile.owl

cat RegisterUnit.owl | sed 's/http:\/\/localhost:8080\/RegisterUnit\/services/http:\/\/localhost:8080\/omsservices\/services/' >../transformado/RegisterUnit.owl
cat RegisterUnitProcess.owl | sed 's/http:\/\/localhost:8080\/RegisterUnit\/services/http:\/\/localhost:8080\/omsservices\/services/'  >../transformado/RegisterUnitProcess.owl
cat RegisterUnitProfile.owl | sed 's/http:\/\/localhost:8080\/RegisterUnit\/services/http:\/\/localhost:8080\/omsservices\/services/' >../transformado/RegisterUnitProfile.owl

cat RegisterNorm.owl | sed 's/http:\/\/localhost:8080\/RegisterNorm\/services/http:\/\/localhost:8080\/omsservices\/services/' >../transformado/RegisterNorm.owl
cat RegisterNormProcess.owl | sed 's/http:\/\/localhost:8080\/RegisterNorm\/services/http:\/\/localhost:8080\/omsservices\/services/'  >../transformado/RegisterNormProcess.owl
cat RegisterNormProfile.owl | sed 's/http:\/\/localhost:8080\/RegisterNorm\/services/http:\/\/localhost:8080\/omsservices\/services/' >../transformado/RegisterNormProfile.owl





