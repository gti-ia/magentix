package norms;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import junit.framework.TestCase;
import es.upv.dsic.gti_ia.norms.*;


public class TestParser extends TestCase {



	Norms parser = null;

	protected void tearDown() throws Exception {


		parser = null;



	}
	protected void setUp() throws Exception {
		super.setUp();





	}

	public void testNorm1()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@acces_RegisternotUnit[p, <positionName:creator>, registerUnit(Uni_tName, _,ParentUnitName, AgentName,_),, ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new Norms(input);

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
			StringBuffer StringBuffer1 = new StringBuffer("@accesDeregisterUnit[p, <positionName:creat_or>,deregisterUnit(UnitName,AgentName),not(playsRole(_ , UnitName,RoleName) & hasPosition(RoleName, UnitName,Position) & Position != creator) & not(isUnit(SubUnitName) & hasParent(SubUnitName,UnitName)), ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new Norms(input);

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

			parser = new Norms(input);

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

			parser = new Norms(input);

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

			parser = new Norms(input);

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

			parser = new Norms(input);

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

			parser = new Norms(input);

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

			parser = new Norms(input);

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

			parser = new Norms(input);

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

			parser = new Norms(input);

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
			StringBuffer StringBuffer1 = new StringBuffer("@ deregisterUnit3 [p, <positionName:member>,deregisterUnit(UnitName, AgentName), not (playsRole( _ , UnitName,RoleName) & hasPosition(RoleName, UnitName,Position)) & Position!=Creador,(playsRole( _ , UnitName,RoleName2) & hasPosition(RoleName2, UnitName,Position2) & Position2 != creator) ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new Norms(input);

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
			StringBuffer StringBuffer1 = new StringBuffer("@registerRole1[p, <positionName:member>,registerRole(UnitName, AgentName, position, visibility, accessibility, creator), hasType(UnitName,hierarchy) & playsRole(AgentName,RoleName, UnitName) & hasPosition(RoleName,creator, otro), not playsRole(AgentName,RoleName, UnitName) ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new Norms(input);

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

	public void testNorm13()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@registerUnitNorm[p,<positionName:member>,registerUnit(UnitName, _ ,ParentUnitName, AgentName, _ ),(playsRole(AgentName, ParentUnitName,RoleName) & isUnit(virtual) & hasPosition(RoleName, ParentUnitName,creator)),not isRole(RoleName,UnitName) ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new Norms(input);

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

	public void testNorm14()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@ deregisterUnitNorm [f,<agentName:omsServer>,deregisterUnit(UnitName, AgentName),(playsRole(AgentName, UnitName, RoleName) & hasPosition(RoleName, UnitName, creator)),not playsRole(AgentName, UnitName,RoleName) ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new Norms(input);

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

	public void testNorm15()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@ registerRoleNorm [f,<roleName:buyer>,registerRole(seller,_,_,_,_,AgentName),hasParent(UnitName,ParentName) & (playsRole(AgentName, ParentName, RoleName) & hasPosition(RoleName, ParentName, creator)),not hasVisibility(RoleName, UnitName,visible) ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new Norms(input);

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

	public void testNorm16()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@ deregisterRoleNorm [f,<positionName:member>,deregisterRole(_,_, AgentName),(not playsRole( _ , UnitName,RoleName) || not hasPosition(RoleName, UnitName,Position)) & Position!=Creador ,(playsRole( _ , UnitName,RoleName2) & hasPosition(RoleName2, UnitName,Position2)) & Position2!=creator ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new Norms(input);

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
	public void testNorm17()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@ registerNormNorm[f,<positionName:member>,registerNorm (_,_,UnitName, AgentName),hasType(UnitName,hierarchy) & playsRole(AgentName,RoleName, UnitName) & hasPosition(RoleName,creator,_),not playsRole(AgentName,RoleName, UnitName) || not isAgent(AgentName) ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new Norms(input);

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

	public void testNorm18()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@ deregisterNormNorm[p,<positionName:supervisor>,deregisterNorm (_,UnitName, AgentName),hasType(UnitName,hierarchy) & playsRole(AgentName,RoleName, UnitName) & hasPosition(RoleName,supervisor, _),not playsRole(AgentName,RoleName, UnitName) ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new Norms(input);

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

	public void testNorm19()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@ allocateRoleNorm[p,<positionName:member>,allocateRole(RoleName, UnitName, _,AgentName),_,_ ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new Norms(input);

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

	public void testNorm20()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@ deallocateRoleNorm[p,<positionName:member>,deallocateRole(RoleName, UnitName, _,AgentName),not (isNorm(NormName,UnitName) & hasContent(NormName, UnitName,[_,_,_,_,_]) & positionCardinality(member, UnitName, Cardinality)) ,(isNorm(NormName,UnitName) & hasContent(NormName,_,_)) ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new Norms(input);

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

	public void testNorm21()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@ joinUnitNorm[p,<positionName:member>,joinUnit(UnitName,ParentName, AgentName),playsRole(AgentName,RoleName,123) & hasPosition(RoleName,UnitName,creator) & playsRole(AgentName,RoleParentName, ParentName) & hasPosition(RoleParentName,ParentName,creator),not (playsRole(AgentName,RoleName,UnitName) & playsRole(AgentName, RoleParentName, ParentName))];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new Norms(input);

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

	public void testNorm22()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@ acquireRoleNorm [f,<positionName:member>,acquireRole(UnitName, RoleName, AgentName),playsRole(AgentName,RoleName,UnitName) & hasAccessibility (RoleName,UnitName,external),_ ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new Norms(input);

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

	public void testNorm23()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@ leaveRoleNorm [f,<positionName:member>,acquireRole(UnitName, RoleName, AgentName),playsRole(AgentName,RoleName,UnitName) & hasAccessibility (RoleName,UnitName,external),_ ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new Norms(input);

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

	public void testNorm24()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@ informAgentRoleNorm [o,<positionName:member>,informAgentRole(omsServer, AgentName),playsRole(AgentName,RoleName,UnitName) & hasAccessibility (RoleName,UnitName,external),_ ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new Norms(input);

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

	public void testNorm25()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@ informMembersNorm[p,<positionName:member>,informMembers(UnitName,buyer,supervisor, AgentName),playsRole(AgentName,RoleName,123) & hasPosition(RoleName,UnitName,creator) & playsRole(AgentName,RoleParentName, ParentName) & hasPosition(RoleParentName,ParentName,creator),not (playsRole(AgentName,RoleName,UnitName) & playsRole(AgentName, RoleParentName,ParentName))];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new Norms(input);

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

	public void testNorm26()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@ informQuantityMembersNorm [f,<positionName:member>,informQuantityMembers(UnitName, RoleName, _,AgentName),playsRole(AgentName,RoleName,UnitName) & hasAccessibility (RoleName,UnitName,external),loquesea < numero & creator <= 5 + 20 & public >=All - 5 & 123 * 5 > 10 & (ABC ** - Uno) < 15 & (UnitName / 20) != 20 & roleCardinality(RoleName, UnitName, Cardinality) & Cardinality < (5 div 1) mod 3 ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new Norms(input);

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

	public void testNorm27()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@ informUnitNorm [o,<positionName:member>,informUnit(UnitName, AgentName),playsRole(AgentName,RoleName,UnitName) &hasAccessibility (RoleName,UnitName,external),_ ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new Norms(input);

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

	public void testNorm28()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@ informUnitRolesNorm[p,<agentName:supervisor>,informUnitRoles(UnitName, AgentName),hasType(UnitName,hierarchy) & playsRole(AgentName,RoleName, UnitName) & hasPosition(RoleName,supervisor, _) & loquequieraponer021 & loquesea(a,B,_),not playsRole(AgentName,RoleName, UnitName) ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new Norms(input);

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

	public void testNorm29()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@ informTargetNormsNorm[f,<roleName:client100>,informTargetNorms (_,_,UnitName, AgentName),hasType(UnitName,hierarchy) & playsRole(AgentName,RoleName, UnitName) & hasPosition(RoleName,supervisor, _) || private & virtual ,not playsRole(AgentName,RoleName, UnitName) ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new Norms(input);

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

	public void testNorm30()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@ informRoleNorm[p,<positionName:misupervisor>,informRole(RoleName,UnitName, AgentName),hasType(UnitName,hierarchy) & playsRole(AgentName,RoleName, UnitName) & hasPosition(RoleName,supervisor, _) & member(importante) & flat,not playsRole(AgentName,RoleName, UnitName) ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new Norms(input);

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

	public void testNorm31()
	{
		try
		{

			StringBuffer StringBuffer1 = new StringBuffer("@informNormNorm[f,<positionName:team>,informNorm(_,UnitName, AgentName),hasType(UnitName,hierarchy) & playsRole(AgentName,RoleName, UnitName) & UnitName & _ & hasPosition(RoleName,supervisor, _) & creator == RoleName & subordinate & external,not playsRole(AgentName,RoleName, UnitName) ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new Norms(input);

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

	public void testNorm32()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@ allocateRole2Norm[p,<positionName:member>,allocateRole(RoleName, UnitName, _,AgentName),isRole(RoleName, UnitName) & roleCardinality(RoleName,UnitName,Cardinality) & Cardinality >5,_ ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new Norms(input);

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

	public void testNorm33()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@ deallocateRole2[p,<positionName:member>,deallocateRole(RoleName, UnitName, _,AgentName),positionCardinality(member, UnitName, Cardinality) & Cardinality==10,_];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new Norms(input);

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

	public void testNorm34()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@ allocateRole3[p,<positionName:member>,allocateRole(RoleName, UnitName, _,AgentName),not (isNorm(NormName, UnitName) & hasContent(NormName, UnitName, [_,<roleName:rol>,registerUnit(_,_,_,_,_),_,_])),isNorm(NormName,UnitName) & hasContent(NormName, UnitName, _)];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new Norms(input);

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
	public void testIncorrectNorm1()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@AccesRegisterUnit[p, <positionName:creator>, registerUnit(UnitName, _ ,ParentUnitName, AgentName, _ ),, ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new Norms(input);

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

			parser = new Norms(input);

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

			parser = new Norms(input);

			parser.parser();

			fail();

		}catch(Exception e)
		{

			System.out.println(e.getMessage());
			assertEquals(e.getMessage(), true, true);

		}

	}

	public void testIncorrectNorm4()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@accesRegisterUnit[p, <positionName:Creator>, registerUnit(UnitName, _ ,ParentUnitName, AgentName, _ ),, ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new Norms(input);

			parser.parser();

			fail();

		}catch(Exception e)
		{

			System.out.println(e.getMessage());
			assertEquals(e.getMessage(), true, true);

		}

	}

	public void testIncorrectNorm5()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@accesRegisterUnit[p, <positionName:creator>, registerUnit( _ ,ParentUnitName, AgentName, _ ),, ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new Norms(input);

			parser.parser();

			fail();

		}catch(Exception e)
		{

			System.out.println(e.getMessage());
			assertEquals(e.getMessage(), true, true);

		}

	}

	public void testIncorrectNorm6()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@accesRegisterUnit[p, <positionName:creator>, registerUnitO(UnitName, _ ,ParentUnitName, AgentName, _ ),, ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new Norms(input);

			parser.parser();

			fail();

		}catch(Exception e)
		{

			System.out.println(e.getMessage());
			assertEquals(e.getMessage(), true, true);

		}

	}


	public void testIncorrectNorm7()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@accesDeregisterUnit[p, <positionName:creator>,deregisterUnit(UnitName,AgentName),no(playsRole( _ , UnitName,RoleName) & hasPosition(RoleName, UnitName,Position) & Position \"== creator) & not(isUnit(SubUnitName) & hasParent(SubUnitName,UnitName)), ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new Norms(input);

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

			parser = new Norms(input);

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

			parser = new Norms(input);

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

			parser = new Norms(input);

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

			parser = new Norms(input);

			parser.parser();

			fail();

		}catch(Exception e)
		{

			System.out.println(e.getMessage());
			assertEquals(e.getMessage(), true, true);

		}

	}



	//ERROR: En el target el valor de value no puede començar por mayuscula.
	public void testIncorrectNorm12()
	{
		try
		{
			StringBuffer StringBuffer1 = new StringBuffer("@ allocateRole3[p,<positionName:member>,allocateRole(RoleName, UnitName, _,AgentName),not (isNorm(NormName, UnitName) & hasContent(NormName, UnitName, [_,<roleName:RoleName>,registerUnit(_,_,_,_,_),_,_])),isNorm(NormName,UnitName) & hasContent(NormName, UnitName, _)];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new Norms(input);

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
			StringBuffer StringBuffer1 = new StringBuffer("@ informQuantityMembers2 [f,<positionName:member>,informQuantityMembers(UnitName, RoleName, _,AgentName),playsRole(AgentName,RoleName,UnitName) & hasAccessibility (RoleName,UnitName,external),isUnit(_)!= creator || loquesea < numero & hasContent(_,_,Uno) == isAgent(Uno) & creator <= 5 + 20 & public >=All - 5 & 123 * 5 & ABC ** - Uno & (UnitName / 20) & roleCardinality(RoleName, UnitName, Cardinality) < (5 div 1) mod 3 ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new Norms(input);

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
			StringBuffer StringBuffer1 = new StringBuffer("@ informTargetNorms2[f,<roleName:client100>,informTargetNorms (_,_,UnitName, AgentName),hasType(UnitName,hierarchy)) & playsRole(AgentName,RoleName, UnitName) &hasPosition(RoleName,supervisor) & supervisor(_,vale,123) ||public(jaja) || private & virtual & participant(jeje,_,123) ,not playsRole(AgentName,RoleName, UnitName) ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			parser = new Norms(input);

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

			parser = new Norms(input);

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

			parser = new Norms(input);

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

			parser = new Norms(input);

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

			parser = new Norms(input);

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

			parser = new Norms(input);

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

			parser = new Norms(input);

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

			parser = new Norms(input);

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

			parser = new Norms(input);

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

			parser = new Norms(input);

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

			parser = new Norms(input);

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

			parser = new Norms(input);

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

			parser = new Norms(input);

			parser.parser();

			fail();


		}catch(Exception e)
		{

			System.out.println(e.getMessage());
			assertEquals(e.getMessage(), true, true);

		}

	}

}
