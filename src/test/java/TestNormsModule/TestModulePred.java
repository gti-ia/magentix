package TestNormsModule;

import static org.junit.Assert.*;
import jason.asSyntax.Literal;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;

//import TestOMS.DatabaseAccess;





import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;
import es.upv.dsic.gti_ia.norms.*;


/**
 * ----------------------------------------------
 * 
 * Test  with   Task#532
 * ----------------------------------------------
 * 
 * @author root
 *
 */
public class TestModulePred {



	private BeliefDataBaseInterface bdbi = null;
	DatabaseAccess dbA = null;

	@After
	public void tearDown() throws Exception {

		//------------------Clean Data Base -----------//
		dbA.executeSQL("DELETE FROM normList");
		dbA.executeSQL("DELETE FROM agentPlayList");
		dbA.executeSQL("DELETE FROM agentList");
		dbA.executeSQL("DELETE FROM roleList WHERE idroleList != 1");
		dbA.executeSQL("DELETE FROM unitHierarchy WHERE idChildUnit != 1");
		dbA.executeSQL("DELETE FROM unitList WHERE idunitList != 1");
		
		bdbi = null;
		dbA = null;



	}
	@Before
	public void setUp() throws Exception {
		//super.setUp();
		
		bdbi = new BeliefDataBaseInterface();
		dbA = new DatabaseAccess();
		
		//------------------Clean Data Base -----------//
		dbA.executeSQL("DELETE FROM normList");
		dbA.executeSQL("DELETE FROM agentPlayList");
		dbA.executeSQL("DELETE FROM agentList");
		dbA.executeSQL("DELETE FROM roleList WHERE idroleList != 1");
		dbA.executeSQL("DELETE FROM unitHierarchy WHERE idChildUnit != 1");
		dbA.executeSQL("DELETE FROM unitList WHERE idunitList != 1");
		

		//----------------------------------- Insert agent List---------------------------
		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('vb')");
		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('bigBrother')");
		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('ea')");
		dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('sv')");

		//----------------------------------- Insert unit List---------------------------

		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('forum',(SELECT idunitType FROM unitType WHERE unitTypeName = 'hierarchy'))");
		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('fraternity',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");
		dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('panel',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");
		
		//----------------------------------- Insert unit hierarchy---------------------------
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = 'forum'))");
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'forum'),(SELECT idunitList FROM unitList WHERE unitName = 'fraternity'))");
		dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'forum'),(SELECT idunitList FROM unitList WHERE unitName = 'panel'))");
		
		//----------------------------------- Insert into roleList---------------------------

		
		
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('moderator',(SELECT idunitList FROM unitList WHERE unitName = 'forum'),"+
				"(SELECT idposition FROM position WHERE positionName = 'supervisor'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
		"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('participant',(SELECT idunitList FROM unitList WHERE unitName = 'forum'),"+
				"(SELECT idposition FROM position WHERE positionName = 'subordinate'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
		"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('brother',(SELECT idunitList FROM unitList WHERE unitName = 'fraternity'),"+
				"(SELECT idposition FROM position WHERE positionName = 'member'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
		"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('president',(SELECT idunitList FROM unitList WHERE unitName = 'fraternity'),"+
				"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
		"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('reporter',(SELECT idunitList FROM unitList WHERE unitName = 'panel'),"+
				"(SELECT idposition FROM position WHERE positionName = 'member'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
		"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");

		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('follower',(SELECT idunitList FROM unitList WHERE unitName = 'panel'),"+
				"(SELECT idposition FROM position WHERE positionName = 'member'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
		"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");
		
		dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
				"('moderator',(SELECT idunitList FROM unitList WHERE unitName = 'panel'),"+
				"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
				"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
		"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");

		
	
		//----------------------------------- Insert agent Play List---------------------------
		
		
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
		"((SELECT idagentList FROM agentList WHERE agentName = 'vb'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
		"((SELECT idagentList FROM agentList WHERE agentName = 'bigBrother'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
		"((SELECT idagentList FROM agentList WHERE agentName = 'ea'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
		"((SELECT idagentList FROM agentList WHERE agentName = 'sv'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");

		
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
		"((SELECT idagentList FROM agentList WHERE agentName = 'vb'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'forum'))))");
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
		"((SELECT idagentList FROM agentList WHERE agentName = 'bigBrother'),(SELECT idroleList FROM roleList WHERE (roleName = 'moderator' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'forum'))))");
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
		"((SELECT idagentList FROM agentList WHERE agentName = 'bigBrother'),(SELECT idroleList FROM roleList WHERE (roleName = 'moderator' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'panel'))))");
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
		"((SELECT idagentList FROM agentList WHERE agentName = 'ea'),(SELECT idroleList FROM roleList WHERE (roleName = 'president' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'fraternity'))))");
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
		"((SELECT idagentList FROM agentList WHERE agentName = 'sv'),(SELECT idroleList FROM roleList WHERE (roleName = 'brother' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'fraternity'))))");
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
		"((SELECT idagentList FROM agentList WHERE agentName = 'ea'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'forum'))))");
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
		"((SELECT idagentList FROM agentList WHERE agentName = 'ea'),(SELECT idroleList FROM roleList WHERE (roleName = 'follower' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'panel'))))");
		dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
		"((SELECT idagentList FROM agentList WHERE agentName = 'sv'),(SELECT idroleList FROM roleList WHERE (roleName = 'reporter' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'panel'))))");

		
		//-------------------------------------------------- Normas -------------------------------------------------
		
		dbA.executeSQL("INSERT INTO `normList` (`idunitList`, `normName`, `iddeontic`, `idtargetType`, `targetValue`, `idactionnorm`, `normContent`, `normRule`) VALUES"+
		"((SELECT idunitList FROM unitList WHERE unitName = 'forum'),'moderatorDerU', (SELECT iddeontic FROM deontic WHERE deonticdesc = 'p'), (SELECT idtargetType FROM targetType WHERE targetName = 'roleName'), (SELECT idroleList FROM roleList WHERE roleName = 'participant' and idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'forum')), (SELECT idactionNorm FROM actionNorm WHERE description = 'deregisterUnit'), 'normContent', 'rule')");
		
		



	}

	//
	@Test (timeout=5000)
	public void testIsUnit()
	{
		try
		{
			ArrayList<Literal> actual = bdbi.getIsUnit();
			
			
			
			assertEquals(true,actual.contains(Literal.parseLiteral("isUnit(virtual)")));
			assertEquals(true,actual.contains(Literal.parseLiteral("isUnit(forum)")));
			assertEquals(true,actual.contains(Literal.parseLiteral("isUnit(fraternity)")));
			assertEquals(true,actual.contains(Literal.parseLiteral("isUnit(panel)")));
			
			assertEquals(4, actual.size());
			

		}catch(Exception e)
		{

			fail(e.getMessage());

		}
		catch(Error e)
		{
			fail(e.getMessage());
		}
	}
	
	@Test (timeout=5000)
	public void testHasType()
	{
		try
		{
			ArrayList<Literal> actual = bdbi.getHasType();
			
			
			
			assertEquals(true,actual.contains(Literal.parseLiteral("hasType(virtual, flat)")));
			assertEquals(true,actual.contains(Literal.parseLiteral("hasType(panel, flat)")));
			assertEquals(true,actual.contains(Literal.parseLiteral("hasType(fraternity, team)")));
			assertEquals(true,actual.contains(Literal.parseLiteral("hasType(forum, hierarchy)")));
			
			
			//assertSame(expected, actual);
			assertEquals(4, actual.size());
			

		}catch(Exception e)
		{

			fail(e.getMessage());

		}
		catch(Error e)
		{
			fail(e.getMessage());
		}
	}
	
	@Test (timeout=5000)
	public void testHasParent()
	{
		try
		{
			ArrayList<Literal> actual = bdbi.getHasParent();
			
	
			
			
			assertEquals(true,actual.contains(Literal.parseLiteral("hasParent(forum, virtual)")));
			assertEquals(true,actual.contains(Literal.parseLiteral("hasParent(fraternity, forum)")));
			assertEquals(true,actual.contains(Literal.parseLiteral("hasParent(panel, forum)")));
			assertEquals(true,actual.contains(Literal.parseLiteral("hasParent(virtual, virtual)")));			
			
			//assertSame(expected, actual);
			assertEquals(4, actual.size());
			

		}catch(Exception e)
		{

			fail(e.getMessage());

		}
		catch(Error e)
		{
			fail(e.getMessage());
		}
	}
	
	@Test (timeout=5000)
	public void testIsRole()
	{
		try
		{
			ArrayList<Literal> actual = bdbi.getIsRole();
			
			
			
			
			assertEquals(8, actual.size());
			
			assertEquals(true,actual.contains(Literal.parseLiteral("isRole(moderator, forum)")));
			assertEquals(true,actual.contains(Literal.parseLiteral("isRole(participant, forum)")));
			assertEquals(true,actual.contains(Literal.parseLiteral("isRole(brother, fraternity)")));			
			assertEquals(true,actual.contains(Literal.parseLiteral("isRole(president, fraternity)")));
			assertEquals(true,actual.contains(Literal.parseLiteral("isRole(reporter, panel)")));
			assertEquals(true,actual.contains(Literal.parseLiteral("isRole(follower, panel)")));
			assertEquals(true,actual.contains(Literal.parseLiteral("isRole(moderator, panel)")));
			assertEquals(true,actual.contains(Literal.parseLiteral("isRole(participant, virtual)")));
			
			
			

		}catch(Exception e)
		{

			fail(e.getMessage());

		}
		catch(Error e)
		{
			fail(e.getMessage());
		}
	}
	
	@Test (timeout=5000)
	public void testHasAccessibility()
	{
		try
		{
			ArrayList<Literal> actual = bdbi.getHasAccessibility();
			
			assertEquals(8, actual.size());
			
			
			assertEquals(true,actual.contains(Literal.parseLiteral("hasAccessibility(participant, virtual, external)")));
			assertEquals(true,actual.contains(Literal.parseLiteral("hasAccessibility(brother, fraternity, external)")));			
			assertEquals(true,actual.contains(Literal.parseLiteral("hasAccessibility(reporter, panel, external)")));
			assertEquals(true,actual.contains(Literal.parseLiteral("hasAccessibility(follower, panel, external)")));
			assertEquals(true,actual.contains(Literal.parseLiteral("hasAccessibility(moderator, forum, internal)")));
			assertEquals(true,actual.contains(Literal.parseLiteral("hasAccessibility(participant, forum, internal)")));
			assertEquals(true,actual.contains(Literal.parseLiteral("hasAccessibility(president, fraternity, internal)")));
			assertEquals(true,actual.contains(Literal.parseLiteral("hasAccessibility(moderator, panel, internal)")));
			
		
			

		}catch(Exception e)
		{

			fail(e.getMessage());

		}
		catch(Error e)
		{
			fail(e.getMessage());
		}
	}
	
	@Test (timeout=5000)
	public void testHasVisibility()
	{
		try
		{
			ArrayList<Literal> actual = bdbi.getHasVisibility();
			
			assertEquals(8, actual.size());
			
			assertEquals(true,actual.contains(Literal.parseLiteral("hasVisibility(moderator, forum, private)")));
			assertEquals(true,actual.contains(Literal.parseLiteral("hasVisibility(president, fraternity, private)")));
			assertEquals(true,actual.contains(Literal.parseLiteral("hasVisibility(reporter, panel, private)")));			
			assertEquals(true,actual.contains(Literal.parseLiteral("hasVisibility(moderator, panel, private)")));
			
			assertEquals(true,actual.contains(Literal.parseLiteral("hasVisibility(participant, virtual, public)")));
			assertEquals(true,actual.contains(Literal.parseLiteral("hasVisibility(participant, forum, public)")));
			assertEquals(true,actual.contains(Literal.parseLiteral("hasVisibility(brother, fraternity, public)")));
			assertEquals(true,actual.contains(Literal.parseLiteral("hasVisibility(follower, panel, public)")));
			
		
			
			
		
			

		}catch(Exception e)
		{

			fail(e.getMessage());

		}
		catch(Error e)
		{
			fail(e.getMessage());
		}
	}
	
	@Test (timeout=5000)
	public void testHasPosition()
	{
		try
		{
			ArrayList<Literal> actual = bdbi.getHasPosition();
			
			assertEquals(8, actual.size());
			
			assertEquals(true,actual.contains(Literal.parseLiteral("hasPosition(participant, virtual, creator)")));
			assertEquals(true,actual.contains(Literal.parseLiteral("hasPosition(president, fraternity, creator)")));
			assertEquals(true,actual.contains(Literal.parseLiteral("hasPosition(moderator, panel, creator)")));
			assertEquals(true,actual.contains(Literal.parseLiteral("hasPosition(brother, fraternity, member)")));
			
			assertEquals(true,actual.contains(Literal.parseLiteral("hasPosition(reporter, panel, member)")));
			assertEquals(true,actual.contains(Literal.parseLiteral("hasPosition(follower, panel, member)")));
			
			assertEquals(true,actual.contains(Literal.parseLiteral("hasPosition(participant, forum, subordinate)")));
			assertEquals(true,actual.contains(Literal.parseLiteral("hasPosition(moderator, forum, supervisor)")));
			
						
			

		}catch(Exception e)
		{

			fail(e.getMessage());

		}
		catch(Error e)
		{
			fail(e.getMessage());
		}
	}
	
	@Test (timeout=5000)
	public void testIsAgent()
	{
		try
		{
			ArrayList<Literal> actual = bdbi.getIsAgent();
			
			assertEquals(4, actual.size());
			
			
			
			assertEquals(true,actual.contains(Literal.parseLiteral("isAgent(bigbrother)")));
			assertEquals(true,actual.contains(Literal.parseLiteral("isAgent(ea)")));
			assertEquals(true,actual.contains(Literal.parseLiteral("isAgent(sv)")));
			assertEquals(true,actual.contains(Literal.parseLiteral("isAgent(vb)")));
			
		
			

		}catch(Exception e)
		{

			fail(e.getMessage());

		}
		catch(Error e)
		{
			fail(e.getMessage());
		}
	}
	
	@Test (timeout=5000)
	public void testPlaysRole()
	{
		try
		{
			ArrayList<Literal> actual = bdbi.getPlaysRole();
			
			assertEquals(12, actual.size());
			
			
			
			assertEquals(true,actual.contains(Literal.parseLiteral("playsRole(vb, participant, virtual)")));
			assertEquals(true,actual.contains(Literal.parseLiteral("playsRole(bigbrother, participant, virtual)")));
			assertEquals(true,actual.contains(Literal.parseLiteral("playsRole(ea, participant, virtual)")));
			assertEquals(true,actual.contains(Literal.parseLiteral("playsRole(sv, participant, virtual)")));
			
			assertEquals(true,actual.contains(Literal.parseLiteral("playsRole(vb,participant ,forum )")));
			assertEquals(true,actual.contains(Literal.parseLiteral("playsRole(bigbrother, moderator, panel)")));
			assertEquals(true,actual.contains(Literal.parseLiteral("playsRole(bigbrother, moderator, panel)")));
			assertEquals(true,actual.contains(Literal.parseLiteral("playsRole(ea,president , fraternity)")));
			assertEquals(true,actual.contains(Literal.parseLiteral("playsRole(sv, brother,fraternity )")));
			assertEquals(true,actual.contains(Literal.parseLiteral("playsRole(ea, participant,forum )")));
			assertEquals(true,actual.contains(Literal.parseLiteral("playsRole(ea, follower, panel)")));
			assertEquals(true,actual.contains(Literal.parseLiteral("playsRole(sv,reporter , panel)")));
			
		
			

		}catch(Exception e)
		{

			fail(e.getMessage());

		}
		catch(Error e)
		{
			fail(e.getMessage());
		}
	}
	
	

	
	
	@Test (timeout=5000)
	public void testRoleCardinality()
	{
		try
		{
			ArrayList<Literal> actual = bdbi.getRoleCardinality();
			
			assertEquals(8, actual.size());
			
			
			
			assertEquals(true,actual.contains(Literal.parseLiteral("roleCardinality(participant, virtual, 4)")));
			assertEquals(true,actual.contains(Literal.parseLiteral("roleCardinality(moderator, forum, 1))")));
			assertEquals(true,actual.contains(Literal.parseLiteral("roleCardinality(participant, forum, 2)")));
			assertEquals(true,actual.contains(Literal.parseLiteral("roleCardinality(brother, fraternity, 1)")));
			assertEquals(true,actual.contains(Literal.parseLiteral("roleCardinality(president,fraternity ,1 )")));
			assertEquals(true,actual.contains(Literal.parseLiteral("roleCardinality(reporter,panel, 1)")));
			assertEquals(true,actual.contains(Literal.parseLiteral("roleCardinality(follower, panel, 1)")));
			assertEquals(true,actual.contains(Literal.parseLiteral("roleCardinality(moderator, panel, 1)")));
			
		
			

		}catch(Exception e)
		{

			fail(e.getMessage());

		}
		catch(Error e)
		{
			fail(e.getMessage());
		}
	}
	
	@Test (timeout=5000)
	public void testPositionCardinality()
	{
		try
		{
			ArrayList<Literal> actual = bdbi.getPositionCardinality();
			
			assertEquals(7, actual.size());
			
			
			
			assertEquals(true,actual.contains(Literal.parseLiteral("positionCardinality(creator, virtual, 4)")));
			//assertEquals(true,actual.contains(Literal.parseLiteral("positionCardinality(member, virtual, 0))")));
			assertEquals(true,actual.contains(Literal.parseLiteral("positionCardinality(supervisor, forum, 1)")));
			assertEquals(true,actual.contains(Literal.parseLiteral("positionCardinality(subordinate, forum, 2)")));
			assertEquals(true,actual.contains(Literal.parseLiteral("positionCardinality(creator,fraternity ,1 )")));
			assertEquals(true,actual.contains(Literal.parseLiteral("positionCardinality(member,fraternity, 1)")));
			assertEquals(true,actual.contains(Literal.parseLiteral("positionCardinality(creator, panel, 1)")));
			assertEquals(true,actual.contains(Literal.parseLiteral("positionCardinality(member, panel, 2)")));
			
		
			

		}catch(Exception e)
		{

			fail(e.getMessage());

		}
		catch(Error e)
		{
			fail(e.getMessage());
		}
	}
	
	@Test (timeout=5000)
	public void testIsNorm()
	{
		try
		{
			ArrayList<Literal> actual = bdbi.getIsNorm();
			
			assertEquals(1, actual.size());
			
			
			
			assertEquals(true,actual.contains(Literal.parseLiteral("isNorm(moderatorderu, forum)")));
						
	
			

		}catch(Exception e)
		{

			fail(e.getMessage());

		}
		catch(Error e)
		{
			fail(e.getMessage());
		}
	}
	
	@Test (timeout=5000)
	public void testHasDeontic()
	{
		try
		{
			ArrayList<Literal> actual = bdbi.getHasDeontic();
			
			assertEquals(1, actual.size());
			
			
			
			assertEquals(true,actual.contains(Literal.parseLiteral("hasDeontic(moderatorderu, forum, p)")));
						
	
			

		}catch(Exception e)
		{

			fail(e.getMessage());

		}
		catch(Error e)
		{
			fail(e.getMessage());
		}
	}
	
	@Test (timeout=5000)
	public void testHasTarget()
	{
		try
		{
			ArrayList<Literal> actual = bdbi.getHasTarget();
			
			assertEquals(1, actual.size());
			
			
			
			assertEquals(true,actual.contains(Literal.parseLiteral("hasTarget(moderatorderu, forum,rolename, participant)")));
						
	
			

		}catch(Exception e)
		{

			fail(e.getMessage());

		}
		catch(Error e)
		{
			fail(e.getMessage());
		}
	}
	
	@Test (timeout=5000)
	public void testHasAction()
	{
		try
		{
			ArrayList<Literal> actual = bdbi.getHasAction();
			
			assertEquals(1, actual.size());
			
			
			
			assertEquals(true,actual.contains(Literal.parseLiteral("hasAction(moderatorderu,forum, deregisterunit)")));
						
	
			

		}catch(Exception e)
		{

			fail(e.getMessage());

		}
		catch(Error e)
		{
			fail(e.getMessage());
		}
	}
	
	@Test (timeout=5000)
	public void testNormRule()
	{
		try
		{
			String actual = bdbi.getNormRule("moderatorderu", "forum");
			
		
			
			
			
			assertEquals("rule", actual);
						
	
			

		}catch(Exception e)
		{

			fail(e.getMessage());

		}
		catch(Error e)
		{
			fail(e.getMessage());
		}
	}
	
	@Test (timeout=5000)
	public void testBuilNormRule()
	{
		try
		{
			Norm norma = null;

			StringBuffer StringBuffer1 = new StringBuffer("@allocateRoleNorm[p,<positionName:member>,allocateRole(RoleName,UnitName,_,AgentName),isRole(RoleName,UnitName) & roleCardinality(RoleName,UnitName,Cardinality) & Cardinality >5,_ ];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			NormParser parser = new NormParser(input);

			norma = parser.parser();
			
			String actual = bdbi.buildNormRule(norma);
			
			assertEquals("allocateRole(RoleName,UnitName,_,AgentName):-isRole(RoleName,UnitName) & roleCardinality(RoleName,UnitName,Cardinality) & Cardinality>5", actual);
						
	
			

		}catch(Exception e)
		{

			fail(e.getMessage());

		}
		catch(Error e)
		{
			fail(e.getMessage());
		}
	}
	
	@Test (timeout=5000)
	public void testBuilNormRule2()
	{
		try
		{
			Norm norma = null;

			StringBuffer StringBuffer1 = new StringBuffer("@allocateRoleNorm[p,<positionName:member>,allocateRole(RoleName,UnitName,_,AgentName),isRole(RoleName,UnitName) & roleCardinality(RoleName,UnitName,Cardinality) & Cardinality > 5,isUnit(virtual)];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			NormParser parser = new NormParser(input);

			norma = parser.parser();
			
			String actual = bdbi.buildNormRule(norma);
			
			assertEquals("allocateRole(RoleName,UnitName,_,AgentName):-isRole(RoleName,UnitName) & roleCardinality(RoleName,UnitName,Cardinality) & Cardinality>5 & not(isUnit(virtual))",actual);
						
		}catch(Exception e)
		{

			fail(e.getMessage());

		}
		catch(Error e)
		{
			fail(e.getMessage());
		}
	}
	
	
	@Test (timeout=5000)
	public void testBuilNormRule3()
	{
		try
		{
			Norm norma = null;

			StringBuffer StringBuffer1 = new StringBuffer("@allocateRoleNorm[p,<positionName:member>,allocateRole(RoleName,UnitName,_,AgentName),_,isUnit(virtual)];");

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

			NormParser parser = new NormParser(input);

			norma = parser.parser();
			
			String actual = bdbi.buildNormRule(norma);
			
		
			
			
			
			
			assertEquals("allocateRole(RoleName,UnitName,_,AgentName):-not(isUnit(virtual))", actual);
						
	
			

		}catch(Exception e)
		{

			fail(e.getMessage());

		}
		catch(Error e)
		{
			fail(e.getMessage());
		}
	}
	
	
}
