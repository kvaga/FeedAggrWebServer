package test.aspectj;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;

public aspect ResponseTimeLogAspect {
//	pointcut mycall(): execution(* *(..));
//	pointcut anyCall(ProceedingJoinPoint joinPoint, String str): execution(* test.aspectj.SampleService.*(..)) && args(joinPoint) && target(ss);
	pointcut anyCall(): execution(* test.aspectj.SampleService.*(..)) ;

	
//	after() returning() : mycall(){
//		System.out.println("after call");
//	}
	
//	Object around(ProceedingJoinPoint joinPoint, String str) throws Throwable: anyCall(joinPoint, str){
		
	void around(): anyCall(){
 
		
		System.out.println("myTrace:before call "
			    +thisJoinPoint.getTarget().getClass().getName()
			    +"."+thisJoinPoint.getSignature().getName());
		System.out.println("before proceed: 1");
		
		Object obj;
		try {
			proceed();
		}finally {
			System.out.println("after proceed: 1");
		}
//		return obj;
	}

}
