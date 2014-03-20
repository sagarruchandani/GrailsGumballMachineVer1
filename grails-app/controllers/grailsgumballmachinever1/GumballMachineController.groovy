package grailsgumballmachinever1

import gumballstate.GumballMachine
import javax.servlet.http.HttpSession


class GumballMachineController {

    def String machineSerialNum = "1234998871109"
    def GumballMachine gumballMachine
    
    def index() {
		
		String VCAP_SERVICES = System.getenv('VCAP_SERVICES')
		
		// Create a session object if it is already not  created.
		//HttpSession session = request.getSession(true);
		
		//def counter= (Integer)setAttribute("count")
        if (request.method == "GET") {
			int i=0
            // search db for gumball machine
            def gumball = Gumball.findBySerialNumber( machineSerialNum )
			//println("Right now there are: "+gumball.countGumballs+" gumballs")
			if ( gumball )
            {
				System.out.println("Hey i m a gumball machine")
				//i++
                // cre	ate a default machine
                gumballMachine = new GumballMachine(gumball.countGumballs)
                gumballMachine.setModelNumber(gumball.modelNumber)
                gumballMachine.setSerialNumber(gumball.serialNumber)
                System.out.println(gumballMachine)
            }
            else
            {
				System.out.println("i should come here only for the first time when there is no gumball")
                // create a default machine
                gumballMachine = new GumballMachine(5);
				gumballMachine.setModelNumber("1115")
				gumballMachine.setSerialNumber("1116")
                System.out.println(gumballMachine)
            }

            // save in the session
            session.machine = gumballMachine
			
            // report a message to user
            flash.message = gumballMachine.toString() 

            // display view
            render(view: "index")

        }
        else if (request.method == "POST") {

            // dump out request object
            request.each { key, value ->
                println( "request: $key = $value")
            }

            // dump out params
            params?.each { key, value ->
                println( "params: $key = $value" )
            }

            // get machine from session
            gumballMachine = session.machine
            System.out.println(gumballMachine)
			
            if ( params?.event == "Insert Quarter" )
            {
                gumballMachine.insertQuarter()
            }
            if ( params?.event == "Turn Crank" )
            {	
				def before = gumballMachine.getCount() ;
				//def before2= (Integer)session.getAttribute("counter");
                gumballMachine.turnCrank();
				def after = gumballMachine.getCount() ;
				//def after2= (Integer)session.getAttribute("counter")
				if ( after != before )
				{
					def gumball = Gumball.findBySerialNumber( machineSerialNum )
					if ( gumball )
					{
						// update gumball inventory
						gumball.countGumballs = after ;
						gumball.save();
					}
				}
				
            }

			
            // report a message to user
            flash.message = gumballMachine.toString() 

            // render view
            render(view: "index")
        }
        else {
            render(view: "/error")
        }
    }

}

