/* -*- Mode: C++; tab-width: 2; indent-tabs-mode: nil; c-basic-offset: 2 -*-
 *
 * The contents of this file are subject to the Netscape Public License
 * Version 1.0 (the "NPL"); you may not use this file except in
 * compliance with the NPL.  You may obtain a copy of the NPL at
 * http://www.mozilla.org/NPL/
 *
 * Software distributed under the NPL is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the NPL
 * for the specific language governing rights and limitations under the
 * NPL.
 *
 * The Initial Developer of this code under the NPL is Netscape
 * Communications Corporation.  Portions created by Netscape are
 * Copyright (C) 1998 Netscape Communications Corporation.  All Rights
 * Reserved.
 */

#include "nsXPFCCanvasManager.h"
#include "nsIXPFCCanvas.h"
#include "nsIView.h"
#include "nsxpfcCIID.h"
#include "nsIViewObserver.h"

static NS_DEFINE_IID(kISupportsIID,         NS_ISUPPORTS_IID);
static NS_DEFINE_IID(kXPFCCanvasManagerIID, NS_IXPFC_CANVAS_MANAGER_IID);
static NS_DEFINE_IID(kCXPFCCanvasIID,       NS_IXPFC_CANVAS_IID);
static NS_DEFINE_IID(kIViewObserverIID,     NS_IVIEWOBSERVER_IID);

class ListEntry {
public:
  nsIView * view;
  nsIXPFCCanvas * canvas;

  ListEntry(nsIView * aView, 
            nsIXPFCCanvas * aCanvas) { 
    view = aView;
    canvas = aCanvas;
  }
  ~ListEntry() {
  }
};


nsXPFCCanvasManager :: nsXPFCCanvasManager()
{
  NS_INIT_REFCNT();
  mList = nsnull;
  monitor = nsnull;
  mRootCanvas = nsnull;
  mFocusedCanvas = nsnull;
  mMouseOverCanvas = nsnull;
  mPressedCanvas = nsnull;
}

nsXPFCCanvasManager :: ~nsXPFCCanvasManager()  
{
  PR_DestroyMonitor(monitor);
  NS_IF_RELEASE(mRootCanvas);
  NS_IF_RELEASE(mList);
}

NS_IMPL_ADDREF(nsXPFCCanvasManager)
NS_IMPL_RELEASE(nsXPFCCanvasManager)

nsresult nsXPFCCanvasManager::QueryInterface(REFNSIID aIID, void** aInstancePtr)      
{                                                                        
  if (NULL == aInstancePtr) {                                            
    return NS_ERROR_NULL_POINTER;                                        
  }                                                                      
  static NS_DEFINE_IID(kISupportsIID,  NS_ISUPPORTS_IID);                 
  static NS_DEFINE_IID(kClassIID, kXPFCCanvasManagerIID);
  if (aIID.Equals(kClassIID)) {                                          
    *aInstancePtr = (void*) (nsXPFCCanvasManager *)this;                                        
    AddRef();                                                            
    return NS_OK;                                                        
  }                                                                      
  if (aIID.Equals(kISupportsIID)) {                                      
    *aInstancePtr = (void*) (this);                        
    AddRef();                                                            
    return NS_OK;                                                        
  }                                                                      
  if (aIID.Equals(kIViewObserverIID)) {
      *aInstancePtr = (void*)((nsIViewObserver*)this);
      AddRef();
      return NS_OK;
  }
  return (NS_ERROR_NO_INTERFACE);
}



nsresult nsXPFCCanvasManager::Init()
{
  if (mList == nsnull) {

    static NS_DEFINE_IID(kCVectorIteratorCID, NS_VECTOR_ITERATOR_CID);
    static NS_DEFINE_IID(kCVectorCID, NS_VECTOR_CID);

    nsresult res = nsRepository::CreateInstance(kCVectorCID, 
                                       nsnull, 
                                       kCVectorCID, 
                                       (void **)&mList);

    if (NS_OK != res)
      return res ;

    mList->Init();
  }

  if (monitor == nsnull) {
    monitor = PR_NewMonitor();
  }

  return NS_OK;
}

nsIXPFCCanvas * nsXPFCCanvasManager::CanvasFromView(nsIView * aView)
{
  nsIXPFCCanvas * canvas = nsnull;

  PR_EnterMonitor(monitor);

  nsIIterator * iterator;

  mList->CreateIterator(&iterator);

  iterator->Init();

  ListEntry * item ;

  while(!(iterator->IsDone()))
  {
    item = (ListEntry *) iterator->CurrentItem();

    if (item->view == aView)
    {
      canvas = item->canvas;
      break;
    }  

    iterator->Next();
  }

  NS_RELEASE(iterator);

  PR_ExitMonitor(monitor);

  return (canvas);
}

nsresult nsXPFCCanvasManager::Register(nsIXPFCCanvas * aCanvas, nsIView * aView)
{
  PR_EnterMonitor(monitor);

  mList->Append(new ListEntry(aView, aCanvas));

  PR_ExitMonitor(monitor);

  return NS_OK;
}

nsresult nsXPFCCanvasManager::Unregister(nsIXPFCCanvas * aCanvas)
{

  PR_EnterMonitor(monitor);

  /*
   * We need to loop through looking for a match of both and then remove them
   */
  nsIIterator * iterator;

  mList->CreateIterator(&iterator);

  iterator->Init();

  ListEntry * item ;

  while(!(iterator->IsDone()))
  {
    item = (ListEntry *) iterator->CurrentItem();

    if (item->canvas == aCanvas)
    {
      mList->Remove((nsComponent)item);
      break;
    }  

    iterator->Next();
  }

  NS_RELEASE(iterator);

  PR_ExitMonitor(monitor);

  return NS_OK;
}

nsresult nsXPFCCanvasManager::GetRootCanvas(nsIXPFCCanvas ** aCanvas)
{
  *aCanvas = mRootCanvas;
  NS_ADDREF(mRootCanvas);
  return NS_OK;
}

nsresult nsXPFCCanvasManager::SetRootCanvas(nsIXPFCCanvas * aCanvas)
{
  NS_IF_RELEASE(mRootCanvas);
  mRootCanvas = aCanvas;
  NS_ADDREF(mRootCanvas);
  return NS_OK;
}

nsresult nsXPFCCanvasManager::SetFocusedCanvas(nsIXPFCCanvas * aCanvas)
{
  mFocusedCanvas = aCanvas;

  mFocusedCanvas->SetFocus();

  return NS_OK;
}

nsIXPFCCanvas * nsXPFCCanvasManager::GetFocusedCanvas()
{
  return(mFocusedCanvas);
}

nsresult nsXPFCCanvasManager::SetPressedCanvas(nsIXPFCCanvas * aCanvas)
{
  mPressedCanvas = aCanvas;
  return NS_OK;
}

nsIXPFCCanvas * nsXPFCCanvasManager::GetPressedCanvas()
{
  return(mPressedCanvas);
}

nsresult nsXPFCCanvasManager::SetMouseOverCanvas(nsIXPFCCanvas * aCanvas)
{
  mMouseOverCanvas = aCanvas;
  return NS_OK;
}

nsIXPFCCanvas * nsXPFCCanvasManager::GetMouseOverCanvas()
{
  return(mMouseOverCanvas);
}

nsresult nsXPFCCanvasManager::Paint(nsIView * aView,
                                    nsIRenderingContext& aRenderingContext,
                                    const nsRect& aDirtyRect)
{
  nsIXPFCCanvas * canvas = CanvasFromView(aView);

  if (canvas == nsnull)
    return NS_OK;

  canvas->OnPaint(aRenderingContext, aDirtyRect);
  
  return NS_OK;
}

nsresult nsXPFCCanvasManager::HandleEvent(nsIView * aView,
                                          nsGUIEvent*     aEvent,
                                          nsEventStatus&  aEventStatus)
{
  nsIXPFCCanvas * canvas = CanvasFromView(aView);

  if (canvas == nsnull)
    return NS_OK;

  aEventStatus = canvas->HandleEvent(aEvent);

  return NS_OK;
}

nsresult nsXPFCCanvasManager::Scrolled(nsIView * aView)
{
  nsIXPFCCanvas * canvas = CanvasFromView(aView);

  if (canvas == nsnull)
    return NS_OK;

  return NS_OK;
}

nsresult nsXPFCCanvasManager::ResizeReflow(nsIView * aView, 
                                           nscoord aWidth, 
                                           nscoord aHeight)
{
  nsIXPFCCanvas * canvas = CanvasFromView(aView);

  if (canvas == nsnull)
    return NS_OK;

  canvas->OnResize(0,0,aWidth,aHeight);

  return NS_OK;
}

