import tkinter as tk


count = 0

def hojadevida(evento):

    global count
    
    print("Comes")    
    print(count)

    if count == 0:
        button.config(text="Yeison Andrey Liscano Ceballes", bg="blue")
        count = 1
        
    elif count == 1:
        button.config(text="Juan Pablo Angel Zuleta", bg="red")
        count = 2
        
    elif count == 2:
        button.config(text="Oscar Fabian Rojas Baquero\n19 años\nciencias de la computacion", bg="green")
        count = 0
            
    print(count)




root = tk.Tk()
root.title("Pyment Manager")
root.geometry("450x350")

button = tk.Button(root, text="Inicio")
button.pack(side="top", anchor="w")

frame1 = tk.Frame(root, bg="red", width=300, height=200)
frame1.place(relx=0.55, rely=0.5, relwidth=.4, relheight=.8,  anchor="w")

frame2 = tk.Frame(root, bg="blue", width=200, height=200)
frame2.place(relx=0.45, rely=0.5, relwidth=.4, relheight=.8,  anchor="e")

frame3 = tk.Frame(frame2)
frame3.place(relx=0, rely=0, relwidth=1, relheight=.4)  

frame4 = tk.Frame(frame1, bg="yellow")
frame4.place(relx=0, rely=0, relwidth=1, relheight=.4)  

frame5 = tk.Frame(frame2, bg="green")
frame5.place(relx=0, rely=.7, relwidth=1, relheight=.3)

button = tk.Button(frame4, text="Oscar Fabian Rojas Baquero\n19 años\nciencias de la computacion", bg="blue", fg="white")
button.pack(fill="both", expand=True)
button.bind("<Button-1>", hojadevida)

button2 = tk.Button(frame5, text="Da clik para ingreasar al sistema", bg="white", fg="black")
button2.pack(fill="both", expand=True)

label = tk.Label(frame3, text="Hola Bienvenido \n a nuestro gestor", bg="white", fg="black")
label.pack(fill="both", expand=True)
root.mainloop()