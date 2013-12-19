package TestNormsModule;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import junit.framework.TestCase;
import es.upv.dsic.gti_ia.norms.NormParser;
import es.upv.dsic.gti_ia.organization.exception.InvalidDeonticException;
import es.upv.dsic.gti_ia.organization.exception.InvalidExpressionException;
import es.upv.dsic.gti_ia.organization.exception.InvalidIDException;
import es.upv.dsic.gti_ia.organization.exception.InvalidOMSActionException;
import es.upv.dsic.gti_ia.organization.exception.InvalidTargetTypeException;
import es.upv.dsic.gti_ia.organization.exception.InvalidTargetValueException;


public class TestParser extends TestCase {



	NormParser parser = null;

	protected void tearDown() throws Exception {
		super.tearDown();

		parser = null;



	}
	protected void setUp() throws Exception {
		super.setUp();

		



	}

	//A partir de la prueba 13 es donde se realizán las pruebas del documento.

	public void testNormForo1()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@controlDeregisterUnit[f, <roleName:moderador>, deregisterUnit(foro,Ag),playsRole(Ag, moderador, foro), ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();


		}catch(Exception e)
		{

			fail(e.getMessage());

		}
		catch(Error e)
		{
			fail(e.getMessage());
		}
	}

	public void testNorm1()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@accesRegisternotUnit[p, <positionName:creator>, registerUnit(team,team,ParentUnitName, AgentName,_),, ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);



			parser.parser();


		}catch(Exception e)
		{

			fail(e.getMessage());

		}
		catch(Error e)
		{
			fail(e.getMessage());
		}
	}

	public void testNorm2()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@accesDeregisterUnit[p, <positionName:creator>,deregisterUnit(UnitName,AgentName),not(playsRole(_ , UnitName,RoleName) & hasPosition(RoleName, UnitName,Position) & Position \\== creator) & not(isUnit(SubUnitName) & hasParent(SubUnitName,UnitName)), ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();


		}catch(Exception e)
		{

			fail(e.getMessage());

		}
		catch(Error e)
		{
			fail(e.getMessage());
		}
	}

	public void testNorm3()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@moderadorDeregisterUnit[ f, <roleName:moderador>, deregisterUnit(foro,Ag), playsRole(Ag, moderador, foro), ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();


		}catch(Exception e)
		{

			fail(e.getMessage());

		}
		catch(Error e)
		{
			fail(e.getMessage());
		}
	}

	public void testNorm4()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@participantDeregisterUnit[ f, <roleName:participante>, deregisterUnit(foro,Ag), playsRole(Ag, participante, foro), ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();


		}catch(Exception e)
		{

			fail(e.getMessage());

		}
		catch(Error e)
		{
			fail(e.getMessage());
		}
	}

	public void testNorm5()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@creatorVirtualDeregisterUnit [f, <positionName:creator>, deregisterUnit(foro, Ag), hasPosition(R1,virtual,creator) & playsRole(Ag, R1, virtual), ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();


		}catch(Exception e)
		{

			fail(e.getMessage());

		}
		catch(Error e)
		{
			fail(e.getMessage());
		}
	}

	public void testNorm6()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@accesDeregisterUnit[p, <agentName:vb>, deregisterUnit(foro,vb), ,];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();


		}catch(Exception e)
		{

			fail(e.getMessage());

		}
		catch(Error e)
		{
			fail(e.getMessage());
		}
	}

	public void testNorm7()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@accesDeregisterUnit[p, <positionName:creator>,deregisterUnit(UnitName,AgentName),not(playsRole( _ , UnitName,RoleName) & hasPosition(RoleName, UnitName,Position) &  hasPosition(RoleName, UnitName,Position)) & not(isUnit(SubUnitName) & hasParent(SubUnitName,UnitName)), ];");

			//quantityMembers(termino, termino, termino, termino)
			//StringBuffer StringBuffer1 = new StringBuffer("@accesDeregisterUnit[p, <positionName:creator>,deregisterUnit(UnitName,AgentName),(9 < 10) & not(isUnit(SubUnitName) & hasParent(SubUnitName,UnitName), ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();


		}catch(Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());

		}
		catch(Error e)
		{
			fail(e.getMessage());
		}
	}

	public void testNorm8()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@registerUnit1[p, <positionName:member>,registerUnit(UnitName, _ ,ParentUnitName, AgentName, _ ), (playsRole(AgentName, ParentUnitName,RoleName) & hasPosition(RoleName, ParentUnitName,creator)),not playsRole(AgentName, ParentUnitName,RoleName) ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();


		}catch(Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());

		}
		catch(Error e)
		{
			fail(e.getMessage());
		}
	}

	public void testNorm9()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@ deregisterUnit1 [p, <positionName:member>,deregisterUnit(UnitName, AgentName), (playsRole(AgentName, UnitName, RoleName) & hasPosition(RoleName, UnitName, creator)),not playsRole(AgentName, UnitName,RoleName) ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();


		}catch(Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());

		}
		catch(Error e)
		{
			fail(e.getMessage());
		}
	}

	public void testNorm10()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@ deregisterUnit2 [p, <positionName:member>,deregisterUnit(UnitName, AgentName), (hasParent(UnitName,ParentName) & (playsRole(AgentName, ParentName, RoleName) & hasPosition(RoleName, ParentName, creator))),not playsRole(AgentName, UnitName,RoleName) ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();


		}catch(Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());

		}
		catch(Error e)
		{
			fail(e.getMessage());
		}
	}

	public void testNorm11()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@ deregisterUnit3 [p, <positionName:member>,deregisterUnit(UnitName, AgentName), not (playsRole( _ , UnitName,RoleName) & hasPosition(RoleName, UnitName,Position)) & Position \\== Creador,(playsRole( _ , UnitName,RoleName2) & hasPosition(RoleName2, UnitName,Position2) & Position2 \\== creator) ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();


		}catch(Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());

		}
		catch(Error e)
		{
			fail(e.getMessage());
		}
	}

	public void testNorm12()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@registerRole1[p, <positionName:member>,registerRole(RoleName, UnitName, external, public, member, creator), hasType(UnitName,hierarchy) & playsRole(AgentName,RoleName, UnitName) & hasPosition(RoleName,UnitName, creator), not playsRole(AgentName,RoleName, UnitName) ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();


		}catch(Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());

		}
		catch(Error e)
		{
			fail(e.getMessage());
		}
	}
	//Descritas en el documento de pruebas.
	public void testNorm1a()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@registerUnitNorm[p,<positionName:member>,registerUnit(UnitName, _ ,ParentUnitName, AgentName, _ ),(playsRole(AgentName, RoleName,ParentUnitName) & isUnit(virtual) & hasPosition(RoleName, ParentUnitName,creator)),not isRole(RoleName,UnitName) ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();


		}catch(Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());

		}
		catch(Error e)
		{
			fail(e.getMessage());
		}
	}

	public void testNorm2a()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@ deregisterUnitNorm [f,<agentName:omsServer>,deregisterUnit(UnitName, AgentName),(playsRole(AgentName, UnitName, RoleName) & hasPosition(RoleName, UnitName, creator)),not playsRole(AgentName, RoleName, UnitName) ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();


		}catch(Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());

		}
		catch(Error e)
		{
			fail(e.getMessage());
		}
	}

	public void testNorm3a()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@ registerRoleNorm [f,<roleName:buyer>,registerRole(seller,_,_,_,_,AgentName),hasParent(UnitName,ParentName) & (playsRole(AgentName,RoleName, ParentName ) & hasPosition(RoleName, ParentName, creator)),not hasVisibility(RoleName, UnitName,public) ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();


		}catch(Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());

		}
		catch(Error e)
		{
			fail(e.getMessage());
		}
	}

	public void testNorm4a()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@ deregisterRoleNorm [f,<positionName:member>,deregisterRole(_,_, AgentName),(not playsRole( _ , RoleName, UnitName) | not hasPosition(RoleName, UnitName,Position)) & Position \\== Creador ,(playsRole( _ , RoleName2, UnitName) & hasPosition(RoleName2, UnitName,Position2)) & Position2 \\== creator ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();


		}catch(Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());

		}
		catch(Error e)
		{
			fail(e.getMessage());
		}
	}
	public void testNorm5a()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@ registerNormNorm[f,<positionName:member>,registerNorm (_,UnitName, _,_,_,_, AgentName),hasType(UnitName,hierarchy) & playsRole(AgentName,RoleName, UnitName) & hasPosition(RoleName,_,creator),not playsRole(AgentName,RoleName, UnitName) | not isAgent(AgentName) ];");
			
			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();


		}catch(Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());

		}
		catch(Error e)
		{
			fail(e.getMessage());
		}
	}

	public void testNorm6a()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@ deregisterNormNorm[p,<positionName:supervisor>,deregisterNorm (_,UnitName, AgentName),hasType(UnitName,hierarchy) & playsRole(AgentName,RoleName, UnitName) & hasPosition(RoleName,_,supervisor),not playsRole(AgentName,RoleName, UnitName) ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();


		}catch(Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());

		}
		catch(Error e)
		{
			fail(e.getMessage());
		}
	}

	public void testNorm7a()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@ allocateRoleNorm[p,<positionName:member>,allocateRole(RoleName, UnitName, _,AgentName),_,_ ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();


		}catch(Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());

		}
		catch(Error e)
		{
			fail(e.getMessage());
		}
	}

	public void testNorm8a()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@ deallocateRoleNorm[p,<positionName:_>,deallocateRole(RoleName, UnitName, _,AgentName),not (isNorm(NormName,UnitName) & positionCardinality(member, UnitName, Cardinality)) ,(isNorm(NormName,UnitName) & hasAction(NormName,_,_)) ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();


		}catch(Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());

		}
		catch(Error e)
		{
			fail(e.getMessage());
		}
	}

	public void testNorm9a()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@ joinUnitNorm[p,<positionName:member>,joinUnit(UnitName, AgentName, ParentName),playsRole(AgentName,RoleName,123) & hasPosition(RoleName,UnitName,creator) & playsRole(AgentName,RoleParentName, ParentName) & hasPosition(RoleParentName,ParentName,creator),not (playsRole(AgentName,RoleName,UnitName) & playsRole(AgentName, RoleParentName, ParentName))];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();


		}catch(Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());

		}
		catch(Error e)
		{
			fail(e.getMessage());
		}
	}

	public void testNorm10a()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@ acquireRoleNorm [f,<positionName:member>,acquireRole(UnitName, RoleName, AgentName),playsRole(AgentName,RoleName,UnitName) & hasAccessibility (RoleName,UnitName,external),_ ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();


		}catch(Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());

		}
		catch(Error e)
		{
			fail(e.getMessage());
		}
	}

	public void testNorm11a()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@ leaveRoleNorm [f,<roleName:_>,acquireRole( RoleName, UnitName,AgentName),playsRole(AgentName,RoleName,UnitName) & hasAccessibility (RoleName,UnitName,external),_ ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();


		}catch(Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());

		}
		catch(Error e)
		{
			fail(e.getMessage());
		}
	}

	public void testNorm12a()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@ informAgentRoleNorm [o,<agentName:_>,informAgentRole(omsServer, AgentName),playsRole(AgentName,RoleName,UnitName) & hasAccessibility (RoleName,UnitName,external),_ ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();


		}catch(Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());

		}
		catch(Error e)
		{
			fail(e.getMessage());
		}
	}

	public void testNorm13a()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@ informMembersNorm[p,<positionName:member>,informMembers(UnitName,buyer,supervisor, AgentName),playsRole(AgentName,RoleName,123) & hasPosition(RoleName,UnitName,creator) & playsRole(AgentName,RoleParentName, ParentName) & hasPosition(RoleParentName,ParentName,creator),not (playsRole(AgentName,RoleName,UnitName) & playsRole(AgentName, RoleParentName,ParentName))];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();


		}catch(Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());

		}
		catch(Error e)
		{
			fail(e.getMessage());
		}
	}

	public void testNorm14a()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@ informQuantityMembersNorm [f,<positionName:member>,informQuantityMembers(UnitName, RoleName, _,AgentName),playsRole(AgentName,RoleName,UnitName) & hasAccessibility (RoleName,UnitName,external),loquesea < numero & creator <= 5 + 20 & public >=All - 5 & 123 * 5 > 10 & (ABC ** - Uno) < 15 & (UnitName / 20) \\== 20 & roleCardinality(RoleName, UnitName, Cardinality) & Cardinality < (5 div 1) mod 3 | Cardinality == 5];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();


		}catch(Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());

		}
		catch(Error e)
		{
			fail(e.getMessage());
		}
	}

	public void testNorm15a()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@ informUnitNorm [o,<positionName:member>,informUnit(UnitName, AgentName),playsRole(AgentName,RoleName,UnitName) & hasAccessibility (RoleName,UnitName,external),_ ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();


		}catch(Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());

		}
		catch(Error e)
		{
			fail(e.getMessage());
		}
	}

	public void testNorm16a()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@ informUnitRolesNorm[p,<agentName:123>,informUnitRoles(UnitName, AgentName),hasType(UnitName,hierarchy) & playsRole(AgentName,RoleName, UnitName) & hasPosition(RoleName,_,supervisor) & loquequieraponer021,not playsRole(AgentName,RoleName, UnitName) ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();


		}catch(Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());

		}
		catch(Error e)
		{
			fail(e.getMessage());
		}
	}

	public void testNorm17a()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@ informTargetNormParserNorm[f,<roleName:client100>,informTargetNorms (roleName,RoleX,UnitName, AgentName),hasType(UnitName,hierarchy) & playsRole(AgentName,RoleName, UnitName) & hasPosition(RoleName,_,supervisor) | private & virtual ,not playsRole(AgentName,RoleName, UnitName) ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();


		}catch(Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());

		}
		catch(Error e)
		{
			fail(e.getMessage());
		}
	}

	public void testNorm18a()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@ informRoleNorm[p,<positionName:supervisor>,informRole(RoleName,UnitName, AgentName),hasType(UnitName,hierarchy) & playsRole(AgentName,RoleName, UnitName) & hasPosition(RoleName,UnitName, _)  & flat,not playsRole(AgentName,RoleName, UnitName) ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();


		}catch(Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());

		}
		catch(Error e)
		{
			fail(e.getMessage());
		}
	}

	public void testNorm19a()
	{
		try
		{

			StringBuffer StringBuffer1 = new StringBuffer("@informNormNorm[f,<positionName:subordinate>,informNorm(_,UnitName, AgentName),hasType(UnitName,hierarchy) & playsRole(AgentName,RoleName, UnitName) & UnitName & _ & hasPosition(RoleName,_, supervisor) & creator == RoleName & subordinate & external,not playsRole(AgentName,RoleName, UnitName) ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();


		}catch(Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());

		}
		catch(Error e)
		{
			fail(e.getMessage());
		}
	}

	public void testNorm20a()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@ allocateRole2Norm[p,<positionName:member>,allocateRole(RoleName, UnitName, _,AgentName),isRole(RoleName, UnitName) & roleCardinality(RoleName,UnitName,Cardinality) & Cardinality >5,_ ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();


		}catch(Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());

		}
		catch(Error e)
		{
			fail(e.getMessage());
		}
	}

	public void testNorm21a()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@ deallocateRole2[p,<positionName:member>,deallocateRole(RoleName, UnitName, _,AgentName),positionCardinality(member, UnitName, 10),_];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();


		}catch(Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());

		}
		catch(Error e)
		{
			fail(e.getMessage());
		}
	}

	public void testNorm22a()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@ allocateRole3[p,<positionName:_>,allocateRole(RoleName, UnitName, _,AgentName),not (isNorm(NormName, UnitName) & hasDeontic(NormName, UnitName, f) & hasTarget(NormName, UnitName, roleName, Rolename) & hasAction(NormName, UnitName, registerUnit)),_];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();


		}catch(Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());

		}
		catch(Error e)
		{
			fail(e.getMessage());
		}
	}



	public void testNorm23a()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@ registerUnitNormBis [f,<agentName:_>, registerUnit(participant,flat,team, public, private), (playsRole(hierarchy,external,internal) & isUnit(virtual) & hasPosition(creator,member,creator)) | not hasDeontic(supervisor, subordinate, f), not isRole(RoleName,UnitName) | hasAction(virtual, participant, acquireRole) ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();


		}catch(Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());

		}
		catch(Error e)
		{
			fail(e.getMessage());
		}
	}


	// Pruebas del manual

	public void testIncorrectNorm1a()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@ informQuantityMembersWrongIsUnit[f,<positionName:member>,informQuantityMembers(UnitName, RoleName, _,AgentName),playsRole(AgentName,RoleName,UnitName) & isUnit(UnitName)== virtual,_];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();

			fail();
		}
		catch(InvalidExpressionException e)
		{
			System.out.println("----------- Prueba 1 ----------");
			System.out.println(e.getMessage());
			System.out.println("--------------------------------");
			assertEquals(e.getMessage(), true, true);

		}catch (Exception e) {

			e.printStackTrace();
		}

	}

	public void testIncorrectNorm2a()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@ informQuantityMembersWrongIsUnit2[f,<positionName:member>,informQuantityMembers(UnitName, RoleName, _,AgentName),playsRole(AgentName,RoleName,UnitName) & UnitName!= virtual,_];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();

			fail();

		}
		catch(Error er)
		{
			System.out.println(er.getMessage());
			assertEquals(er.getMessage(), true, true);

		}catch(Exception e)
		{

			System.out.println(e.getMessage());
			assertEquals(e.getMessage(), true, true);

		}

	}

	public void testIncorrectNorm3a()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@ informQuantityMembersWrongIsUnit3[f,<positionName:member>,informQuantityMembers(UnitName, RoleName, _,AgentName),playsRole(AgentName,RoleName,UnitName) || UnitName\\== virtual,_];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();

			fail();
		}
		catch(InvalidExpressionException e)
		{
			System.out.println("----------- Prueba 3 ----------");
			System.out.println(e.getMessage());
			System.out.println("--------------------------------");
			assertEquals(e.getMessage(), true, true);

		}catch (Exception e) {

			e.printStackTrace();
		}

	}

	public void testIncorrectNorm4a()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@ informQuantityMembersWrongNumber [f,<positionName:member>,informQuantityMembers(UnitName, RoleName, _,AgentName),123,_];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();

			fail();
		}
		catch(InvalidExpressionException e)
		{
			System.out.println("----------- Prueba 4a----------");
			System.out.println(e.getMessage());
			System.out.println("--------------------------------");
			assertEquals(e.getMessage(), true, true);

		}catch (Exception e) {

			e.printStackTrace();
		}

	}

	public void testIncorrectNorm4b()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@ informQuantityMembersWrongNumber [f,<positionName:member>,informQuantityMembers(UnitName, RoleName, _,AgentName),ABC * 5,_];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();

			fail();
		}
		catch(InvalidExpressionException e)
		{
			System.out.println("----------- Prueba 4b ----------");
			System.out.println(e.getMessage());
			System.out.println("--------------------------------");
			assertEquals(e.getMessage(), true, true);

		}catch (Exception e) {

			e.printStackTrace();
		}

	}

	public void testIncorrectNorm5a()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@ informTargetNormsWrong1[f,<roleName:client100>,informTargetNorms (_,_,UnitName, AgentName),hasType(UnitName,hierarchy)) & playsRole(AgentName,RoleName, UnitName) ,_ ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();

			fail();
		}
		catch(InvalidExpressionException e)
		{
			System.out.println("----------- Prueba 5 ----------");
			System.out.println(e.getMessage());
			System.out.println("--------------------------------");
			assertEquals(e.getMessage(), true, true);

		}catch (Exception e) {

			e.printStackTrace();
		}

	}

	public void testIncorrectNorm6a()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@ informTargetNormsWrong2[f,<roleName:client100>,informTargetNorms (_,_,UnitName, AgentName),hasType(UnitName,hierarchy) & playsRole(AgentName,RoleName, UnitName) & hasPosition(RoleName,supervisor), _ ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();

			fail();
		}
		catch(InvalidExpressionException e)
		{
			System.out.println("----------- Prueba 6 ----------");
			System.out.println(e.getMessage());
			System.out.println("--------------------------------");
			assertEquals(e.getMessage(), true, true);

		}catch (Exception e) {

			e.printStackTrace();
		}

	}

	public void testIncorrectNorm7a()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@ informTargetNormsWrong3[f,<roleName:client100>,informTargetNorms (_,_,UnitName, AgentName),hasType(UnitName,hierarchy) & playsRole(AgentName,RoleName, UnitName) &hasPosition(RoleName,supervisor, Value) & supervisor(_,vale,123),_ ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();

			fail();
		}
		catch(InvalidExpressionException e)
		{
			System.out.println("----------- Prueba 7 ----------");
			System.out.println(e.getMessage());
			System.out.println("--------------------------------");
			assertEquals(e.getMessage(), true, true);

		}catch (Exception e) {

			e.printStackTrace();
		}

	}

	public void testIncorrectNorm8a()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@ informRoleWrong1[p,<positionName:misupervisor>,informRole(RoleName,UnitName, AgentName),_, _ ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();

			fail();
		}
		catch(InvalidTargetValueException e)
		{
			System.out.println("----------- Prueba 8 ----------");
			System.out.println(e.getMessage());
			System.out.println("--------------------------------");
			assertEquals(e.getMessage(), true, true);

		}catch (Exception e) {

			e.printStackTrace();
		}
		catch(Error er)
		{
			System.out.println(er.getMessage());
			assertEquals(er.getMessage(), true, true);

		}

	}

	public void testIncorrectNorm9a()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@ informRoleWrong2[p,<positionName:supervisor>,informRole(RoleName,UnitName, AgentName),hasType(UnitName,jerarquia),_];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();

			fail();
		}
		catch(InvalidExpressionException e)
		{
			System.out.println("----------- Prueba 9 ----------");
			System.out.println(e.getMessage());
			System.out.println("--------------------------------");
			assertEquals(e.getMessage(), true, true);

		}catch (Exception e) {

			e.printStackTrace();
		}

	}

	public void testIncorrectNorm10a()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@ informRoleWrong3[p,<positionName:supervisor>,informRole(RoleName,UnitName, AgentName),hasType(UnitName,hierarchy) & playsRole(AgentName,RoleName),_) ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();

			fail();
		}
		catch(InvalidExpressionException e)
		{
			System.out.println("----------- Prueba 10 ----------");
			System.out.println(e.getMessage());
			System.out.println("--------------------------------");
			assertEquals(e.getMessage(), true, true);

		}catch (Exception e) {

			e.printStackTrace();
		}

	}

	public void testIncorrectNorm11a()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@informNormWrong1[f,<positionName:team>,informNorm(_,UnitName, AgentName),_, _ ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();

			fail();
		}
		catch(InvalidTargetValueException e)
		{
			System.out.println("----------- Prueba 11 ----------");
			System.out.println(e.getMessage());
			System.out.println("--------------------------------");
			assertEquals(e.getMessage(), true, true);

		}catch (Exception e) {

			e.printStackTrace();
		}
		catch(Error er)
		{
			System.out.println(er.getMessage());
			assertEquals(er.getMessage(), true, true);

		}

	}

	public void testIncorrectNorm12a()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@informNormWrong2[f,<positionName:_>,informNorm(_,UnitName, AgentName),hasType(UnitName,hierarchy) & playsRole(AgentName,RoleName, UnitName) &ParentName(hola,_,UnitName) ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();

			fail();
		}
		catch(InvalidExpressionException e)
		{
			System.out.println("----------- Prueba 12 ----------");
			System.out.println(e.getMessage());
			System.out.println("--------------------------------");
			assertEquals(e.getMessage(), true, true);

		}catch (Exception e) {

			e.printStackTrace();
		}

	}

	public void testIncorrectNorm13a()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@informUnitRolesNormWrong1[p,<agentName:supervisor>,informUnitRoles(UnitName, AgentName),_,_];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();

			fail();
		}
		catch(InvalidTargetValueException e)
		{
			System.out.println("----------- Prueba 13 ----------");
			System.out.println(e.getMessage());
			System.out.println("--------------------------------");
			assertEquals(e.getMessage(), true, true);

		}catch (Exception e) {

			e.printStackTrace();
		}

	}

	public void testIncorrectNorm14a()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@informUnitRolesNormWrong1[p,<positionName:123>,informUnitRoles(UnitName, AgentName),_,_];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();

			fail();
		}
		catch(InvalidTargetValueException e)
		{
			System.out.println("----------- Prueba 14 ----------");
			System.out.println(e.getMessage());
			System.out.println("--------------------------------");
			assertEquals(e.getMessage(), true, true);

		}catch (Exception e) {

			e.printStackTrace();
		}
		catch(Error er)
		{
			System.out.println(er.getMessage());
			assertEquals(er.getMessage(), true, true);

		}
	}

	public void testIncorrectNorm15a()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("informUnitRolesNormWrong2[p,<positionName:_>,informUnitRoles(UnitName, AgentName),_,_];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();

			fail();
		}
		catch(InvalidExpressionException e)
		{
			System.out.println("----------- Prueba 15 ----------");
			System.out.println(e.getMessage());
			System.out.println("--------------------------------");
			assertEquals(e.getMessage(), true, true);

		}catch (Exception e) {

			e.printStackTrace();
		}

	}

	public void testIncorrectNorm16a()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@[p,<positionName:_>,informUnitRoles(UnitName, AgentName),_,_];");

			InputStream input;

			input = new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));


			parser = new NormParser(input);

			parser.parser();

			fail();

		}
		catch(InvalidIDException e)
		{
			System.out.println("----------- Prueba 16 ----------");
			System.out.println(e.getMessage());
			System.out.println("--------------------------------");
			assertEquals(e.getMessage(), true, true);

		}catch (Exception e) {

			e.printStackTrace();
		}
		catch(Error er)
		{
			System.out.println(er.getMessage());
			assertEquals(er.getMessage(), true, true);

		}

	}

	public void testIncorrectNorm17a()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@informUnitRolesWrongp,<positionName:_>,informUnitRoles(UnitName, AgentName),_,_];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();

			fail();
		}
		catch(InvalidExpressionException e)
		{
			System.out.println("----------- Prueba 17 ----------");
			System.out.println(e.getMessage());
			System.out.println("--------------------------------");
			assertEquals(e.getMessage(), true, true);

		}catch (Exception e) {

			e.printStackTrace();
		}

	}

	public void testIncorrectNorm18a()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@informUnitRolesWrong[p,<positionName:_>,informUnitRoles(UnitName, AgentName),_];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();

			fail();
		
		}catch (Error e) {
		
			e.printStackTrace();
		}
		
		catch(InvalidExpressionException e)
		{
			System.out.println("----------- Prueba 18 ----------");
			System.out.println(e.getMessage());
			System.out.println("--------------------------------");
			assertEquals(e.getMessage(), true, true);

		}catch (Exception e) {

			e.printStackTrace();
		}

	}

	public void testIncorrectNorm19a()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@123N[p,<positionName:_>,informUnitRoles(UnitName, AgentName),_, _];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();

			fail();
		}

		catch(InvalidIDException e)
		{
			System.out.println("----------- Prueba 19 ----------");
			System.out.println(e.getMessage());
			System.out.println("--------------------------------");
			assertEquals(e.getMessage(), true, true);

		}catch (Exception e) {

			e.printStackTrace();
		}
		catch(Error er)
		{
			System.out.println(er.getMessage());
			assertEquals(er.getMessage(), true, true);

		}
	}

	public void testIncorrectNorm20a()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@norm1[forbidden,<positionName:_>,informUnitRoles(UnitName, AgentName),_, _];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();

			fail();
		}

		catch(InvalidDeonticException e)
		{
			System.out.println("----------- Prueba 20 ----------");
			System.out.println(e.getMessage());
			System.out.println("--------------------------------");
			assertEquals(e.getMessage(), true, true);

		}catch (Exception e) {

			e.printStackTrace();
		}
		catch(Error er)
		{
			System.out.println(er.getMessage());
			assertEquals(er.getMessage(), true, true);

		}

	}

	public void testIncorrectNorm21a()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@norm1[p,positionName:_,informUnitRoles(UnitName, AgentName),_, _];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();

			fail();
		}
		catch(InvalidTargetTypeException e)
		{
			System.out.println("----------- Prueba 21 ----------");
			System.out.println(e.getMessage());
			System.out.println("--------------------------------");
			assertEquals(e.getMessage(), true, true);

		}catch (Exception e) {

			e.printStackTrace();
		}
		catch(Error er)
		{
			System.out.println(er.getMessage());
			assertEquals(er.getMessage(), true, true);

		}

	}

	public void testIncorrectNorm22a()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@norm1[p,<invalid:_>,informUnitRoles(UnitName, AgentName),_, _];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();

			fail();
		}
		catch(InvalidTargetTypeException e)
		{
			System.out.println("----------- Prueba 22 ----------");
			System.out.println(e.getMessage());
			System.out.println("--------------------------------");
			assertEquals(e.getMessage(), true, true);

		}catch (Exception e) {

			e.printStackTrace();
		}
		catch(Error er)
		{
			System.out.println(er.getMessage());
			assertEquals(er.getMessage(), true, true);

		}

	}
	/**
	 * The action is not a valid oms action.
	 */
	public void testIncorrectNorm23a()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@norm1[p,<positionName:_>,informUnitRole(UnitName, AgentName),_, _];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();

			fail();
		}
		catch(InvalidOMSActionException e)
		{
			System.out.println("----------- Prueba 23 ----------");
			System.out.println(e.getMessage());
			System.out.println("--------------------------------");
			assertEquals(e.getMessage(), true, true);

		}catch (Exception e) {

			e.printStackTrace();
		}
		catch(Error er)
		{
			System.out.println(er.getMessage());
			assertEquals(er.getMessage(), true, true);

		}

	}
	/*
	public void testIncorrectNorm1()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@AccesRegisterUnit[p, <positionName:creator>, registerUnit(UnitName, _ ,ParentUnitName, AgentName, _ ),, ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();

			fail();

		}catch(Exception e)
		{

			System.out.println(e.getMessage());
			assertEquals(e.getMessage(), true, true);

		}

	}

	public void testIncorrectNorm2()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@accesRegisterUnit[i, <positionName:creator>, registerUnit(UnitName, _ ,ParentUnitName, AgentName, _ ),, ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();

			fail();

		}catch(Exception e)
		{

			System.out.println(e.getMessage());
			assertEquals(e.getMessage(), true, true);

		}

	}

	public void testIncorrectNorm3()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@accesRegisterUnit[p, <positionNameOtro:creator>, registerUnit(UnitName, _ ,ParentUnitName, AgentName, _ ),, ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();

			fail();

		}catch(Exception e)
		{


			assertEquals(e.getMessage(), true, true);

		}

	}

	public void testIncorrectNorm4()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@accesRegisterUnit[p, <positionName:Creator>, registerUnit(UnitName, _ ,ParentUnitName, AgentName, _ ),, ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();

			fail();

		}catch(Exception e)
		{


			assertEquals(e.getMessage(), true, true);

		}

	}

	public void testIncorrectNorm5()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@accesRegisterUnit[p, <positionName:creator>, registerUnit( _ ,ParentUnitName, AgentName, _ ),, ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();

			fail();

		}catch(Exception e)
		{


			assertEquals(e.getMessage(), true, true);

		}

	}

	public void testIncorrectNorm6()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@accesRegisterUnit[p, <positionName:creator>, registerUnitO(UnitName, _ ,ParentUnitName, AgentName, _ ),, ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();

			fail();

		}catch(Exception e)
		{


			assertEquals(e.getMessage(), true, true);

		}

	}


	public void testIncorrectNorm7()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@accesDeregisterUnit[p, <positionName:creator>,deregisterUnit(UnitName,AgentName),no(playsRole( _ , UnitName,RoleName) & hasPosition(RoleName, UnitName,Position) & Position \"== creator) & not(isUnit(SubUnitName) & hasParent(SubUnitName,UnitName)), ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();

			fail();

		}catch(Exception e)
		{

			System.out.println(e.getMessage());
			assertEquals(e.getMessage(), true, true);

		}

	}


	public void testIncorrectNorm8()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@accesDeregisterUnit[p, <positionName:creator>,deregisterUnit(UnitName,AgentName),not(playsRole( _ , UnitName,RoleName) & hasPosition(RoleName, UnitName,Position) & Position \"== creator) & not(isUnit(SubUnitName) & hasParent(SubUnitName,UnitName), ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();

			fail();

		}catch(Exception e)
		{

			System.out.println(e.getMessage());
			assertEquals(e.getMessage(), true, true);

		}
		catch(Error e)
		{

			System.out.println(e.getMessage());
			assertEquals(e.getMessage(), true, true);
		}
	}

	public void testIncorrectNorm9()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@accesDeregisterUnit[p, <positionName:creator>,deregisterUnit(UnitName,AgentName),not(playsRole( _ , UnitName,RoleName) & hasPosition( UnitName,Position) & Position \"== creator) & not(isUnit(SubUnitName) & hasParent(SubUnitName,UnitName)), ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();

			fail();

		}catch(Exception e)
		{

			System.out.println(e.getMessage());
			assertEquals(e.getMessage(), true, true);

		}

	}

	public void testIncorrectNorm10()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@ informQuantityMembersNorm [f,<positionName:member>,informQuantityMembers(UnitName, RoleName, _,AgentName),playsRole(AgentName,RoleName,UnitName) & hasAccessibility (RoleName,UnitName,external),loquesea < numero & creator <= 5 + 20 & public >=All - 5 & 123 * 5 & ABC ** - Uno & (UnitName / 20) & roleCardinality(RoleName, UnitName, Cardinality) & Cardinality < (5 div 1) mod 3 ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();

			fail();

		}catch(Exception e)
		{

			System.out.println(e.getMessage());
			assertEquals(e.getMessage(), true, true);

		}

	}
	public void testIncorrectNorm11()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@ informUnitRolesNorm[p,<agentName:supervisor>,informUnitRoles(UnitName, AgentName),hasType(UnitName,hierarchy) & playsRole(AgentName,RoleName, UnitName) & hasPosition(RoleName,supervisor) & loquequieraponer021 & loquesea(a,B,_),not playsRole(AgentName,RoleName, UnitName) ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();

			fail();

		}catch(Exception e)
		{

			System.out.println(e.getMessage());
			assertEquals(e.getMessage(), true, true);

		}

	}





	public void testIncorrectNorm13()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@ informQuantityMembers2 [f,<positionName:member>,informQuantityMembers(UnitName, RoleName, _,AgentName),playsRole(AgentName,RoleName,UnitName) & hasAccessibility (RoleName,UnitName,external),isUnit(_) \\== creator | loquesea < numero & hasContent(_,_,Uno) == isAgent(Uno) & creator <= 5 + 20 & public >=All - 5 & 123 * 5 & ABC ** - Uno & (UnitName / 20) & roleCardinality(RoleName, UnitName, Cardinality) < (5 div 1) mod 3 ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();

			fail();


		}catch(Exception e)
		{

			System.out.println(e.getMessage());
			assertEquals(e.getMessage(), true, true);

		}

	}
	public void testIncorrectNorm14()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@ informTargetNormParser2[f,<roleName:client100>,informTargetNormParser (_,_,UnitName, AgentName),hasType(UnitName,hierarchy)) & playsRole(AgentName,RoleName, UnitName) &hasPosition(RoleName,supervisor) & supervisor(_,vale,123) ||public(jaja) || private & virtual & participant(jeje,_,123) ,not playsRole(AgentName,RoleName, UnitName) ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();

			fail();


		}catch(Exception e)
		{

			System.out.println(e.getMessage());
			assertEquals(e.getMessage(), true, true);

		}

	}

	public void testIncorrectNorm15()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@ informRole2[p,<positionName:misupervisor>,informRole(RoleName,UnitName, AgentName),hasType(UnitName,hierarchy) & playsRole(AgentName,RoleName, UnitName) &hasPosition(RoleName,supervisor) & member(importante) & internal(ojo, _) &flat & team(Unit) & hierarchy(UnitName,ParentName),not playsRole(AgentName,RoleName, UnitName) ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();

			fail();


		}catch(Exception e)
		{

			System.out.println(e.getMessage());
			assertEquals(e.getMessage(), true, true);

		}

	}

	public void testIncorrectNorm16()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@informNorm2[f,<positionName:team>,informNorm(_,UnitName, AgentName),hasType(UnitName,hierarchy)) & playsRole(AgentName,RoleName, UnitName) &UnitName & _ & ParentName(hola,_,UnitName) &hasPosition(RoleName,supervisor) & creator & subordinate & external,not playsRole(AgentName,RoleName, UnitName) ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();

			fail();


		}catch(Exception e)
		{

			System.out.println(e.getMessage());
			assertEquals(e.getMessage(), true, true);

		}

	}
	//ERROR: El id empieza por un número.
	public void testIncorrectNorm17()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@12acces_RegisternotUnit[p, <positionName:creator>, registerUnit(Uni_tName, _,ParentUnitName, AgentName,_),, ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();

			fail();


		}catch(Exception e)
		{

			System.out.println(e.getMessage());
			assertEquals(e.getMessage(), true, true);

		}

	}

	//ERROR: El deontinc distinto de f, o, p
	public void testIncorrectNorm18()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@acces_RegisternotUnit[i, <positionName:creator>, registerUnit(Uni_tName, _,ParentUnitName, AgentName,_),, ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();

			fail();


		}catch(Exception e)
		{

			System.out.println(e.getMessage());
			assertEquals(e.getMessage(), true, true);

		}

	}

	//ERROR: no incluye dos elementos entre < >
	public void testIncorrectNorm19()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@acces_RegisternotUnit[p, <:creator>, registerUnit(Uni_tName, _,ParentUnitName, AgentName,_),, ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();

			fail();


		}catch(Exception e)
		{

			System.out.println(e.getMessage());
			assertEquals(e.getMessage(), true, true);

		}

	}

	//ERROR: Los elementos dentro de < > no van separados por :
	public void testIncorrectNorm20()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@acces_RegisternotUnit[p, <positionName,creator>, registerUnit(Uni_tName, _,ParentUnitName, AgentName,_),, ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();

			fail();


		}catch(Exception e)
		{

			System.out.println(e.getMessage());
			assertEquals(e.getMessage(), true, true);

		}

	}

	//ERROR: No tiene un targetType valido
	public void testIncorrectNorm21()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@acces_RegisternotUnit[p, <algo,creator>, registerUnit(Uni_tName, _,ParentUnitName, AgentName,_),, ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();

			fail();


		}catch(Exception e)
		{

			System.out.println(e.getMessage());
			assertEquals(e.getMessage(), true, true);

		}

	}

	//ERROR: El value empieza por una letra.
	public void testIncorrectNorm22()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@acces_RegisternotUnit[p, <positionName,25creator>, registerUnit(Uni_tName, _,ParentUnitName, AgentName,_),, ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();

			fail();


		}catch(Exception e)
		{

			System.out.println(e.getMessage());
			assertEquals(e.getMessage(), true, true);

		}

	}

	//ERROR: La norma no empieza por @.
	public void testIncorrectNorm23()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("acces_RegisternotUnit[p, <positionName,creator>, registerUnit(Uni_tName, _,ParentUnitName, AgentName,_),, ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();

			fail();


		}catch(Exception e)
		{

			System.out.println(e.getMessage());
			assertEquals(e.getMessage(), true, true);

		}

	}

	//ERROR: La norma no tiene identificador.
	public void testIncorrectNorm24()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@[p, <positionName,creator>, registerUnit(Uni_tName, _,ParentUnitName, AgentName,_),, ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();

			fail();


		}catch(Exception e)
		{

			System.out.println(e.getMessage());
			assertEquals(e.getMessage(), true, true);

		}

	}

	//ERROR: La norma Le falta el corchete de delante.
	public void testIncorrectNorm25()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@acces_RegisternotUnit p, <positionName,creator>, registerUnit(Uni_tName, _,ParentUnitName, AgentName,_),, ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();

			fail();


		}catch(Exception e)
		{

			System.out.println(e.getMessage());
			assertEquals(e.getMessage(), true, true);

		}

	}

	//ERROR: La norma Le falta el corchete de detras.
	public void testIncorrectNorm26()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@acces_RegisternotUnit[ p, <positionName,creator>, registerUnit(Uni_tName, _,ParentUnitName, AgentName,_),, ;");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new NormParser(input);

			parser.parser();

			fail();


		}catch(Exception e)
		{

			System.out.println(e.getMessage());
			assertEquals(e.getMessage(), true, true);

		}

	}*/

}
