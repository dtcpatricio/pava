package ist.meic.pa;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.Translator;
import javassist.expr.ConstructorCall;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import javassist.expr.NewExpr;

public class MyTranslator implements Translator {
	@Override
	public void onLoad(ClassPool pool, String className) throws NotFoundException,
			CannotCompileException {
		CtClass ctClass = pool.get(className);
		//constructorCall(ctClass);
	  if(className.equals("ist.meic.pa.Test.Test")) {
			newTrace(ctClass);
			methodTrace(ctClass);
		/*	
			System.out.println("Class name = " + className);
			if(className.equals("ist.meic.pa.Test.Test")) {
				CtMethod m = ctClass.getDeclaredMethod("identity");
				String returnType = m.getReturnType().getName();
				System.out.println(("Return Type = " + returnType));
				m.insertAfter("System.out.println(\"returned object=\" + ($_) );");
				m.insertBefore("{ System.out.println(\"The called argument in identity is:\" + $1); }");
			}
			if(className.equals("ist.meic.pa.Test.Test")) {
				CtMethod m = ctClass.getDeclaredMethod("foo");
				String returnType = m.getReturnType().getName();
				System.identityHashCode(returnType);
				System.out.println(("Return Type = " + returnType));
				m.insertAfter("System.out.println(\"*returned object* \" + \"$0=\" + $0 + \" \" + 	System.identityHashCode($type));");
			} */
		}
	}

	@Override
	public void start(ClassPool arg0) throws NotFoundException,
			CannotCompileException {
		// TODO Auto-generated method stub
	}
	
	public void constructorCall(CtClass ctClass) throws CannotCompileException {
		for(CtMethod ctMethod : ctClass.getDeclaredMethods()) {
			ctMethod.instrument(new ExprEditor() {
				public void edit(ConstructorCall cc) {
					System.err.println("Contructor called");
				}
			});
		}
	}
	
				
	// TODO: Change method name
	// TODO: Different Translators for different methods, fields, constructors?
	// TODO: Format system out
	void methodTrace(CtClass ctClass) throws CannotCompileException, NotFoundException {
		for(final CtMethod ctMethod : ctClass.getDeclaredMethods()) {
			if(ctMethod.getName().equals("foo2")) {
				CtField f = CtField.make("public int Z = 0;", ctClass);
				ctClass.addField(f);
				ctMethod.insertBefore("{ Z = System.identityHashCode($1); System.out.println(\"Hashing inside foo2 \" + Z );"
						+ " iden.functionCall(Z); }");
			}
			ctMethod.instrument(new ExprEditor() {
				public void edit(MethodCall mc) 
					throws CannotCompileException {
					try {
						System.out.println("\t" + mc.getMethod().getLongName() + 
								" on " + mc.getFileName() + 
								":" + mc.getLineNumber());
					} catch (NotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		}
	}
	
	void newTrace(CtClass ctClass) throws CannotCompileException {
		for(final CtMethod ctMethod : ctClass.getDeclaredMethods()) {
			ctMethod.instrument(new ExprEditor() {
				public void edit(NewExpr ne) 
					throws CannotCompileException {
					try {
							// TODO: Insert after in NewExpr ne
							/*ctMethod.insertAfter("iden.addInspectedObject(System.identityHashCode($_), $_); "
									+ "System.out.println(Thread.currentThread().getStackTrace()[1].getLineNumber());"); */
						
						
						System.err.println("'New' Line: " + ne.getLineNumber() +
								" Function calling: " + ne.where().getName()  
								+ " Constructor name: " + ne.getConstructor().getLongName());
					
					} catch (NotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		}
	}
}
