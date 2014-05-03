package ist.meic.pa;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.Translator;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

public class MyTranslator implements Translator {
	@Override
	public void onLoad(ClassPool pool, String className) throws NotFoundException,
			CannotCompileException {
		CtClass ctClass = pool.get(className);
		makeUndoable(ctClass);
	}

	@Override
	public void start(ClassPool arg0) throws NotFoundException,
			CannotCompileException {
		// TODO Auto-generated method stub
	}
	
	// TODO: Change method name
	// TODO: Different Translators for different methods, fields, constructors?
	// TODO: Format system out
	void makeUndoable(CtClass ctClass) throws CannotCompileException {
		for(CtMethod ctMethod : ctClass.getDeclaredMethods()) {
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
}
