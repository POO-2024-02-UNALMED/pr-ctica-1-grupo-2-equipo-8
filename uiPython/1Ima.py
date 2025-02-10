import tkinter as tk

root = tk.Tk()
root.title("Pyment Manager")
root.geometry("400x300")

button = tk.Button(root, text="Inicio")
button.pack(side="top", anchor="w")

frame1 = tk.Frame(root, bg="red", width=300, height=200)
frame1.place(relx=0.55, rely=0.5, relwidth=.4, relheight=.8,  anchor="w")

frame2 = tk.Frame(root, bg="blue", width=200, height=200)
frame2.place(relx=0.45, rely=0.5, relwidth=.4, relheight=.8,  anchor="e")

frame3 = tk.Frame(frame2, bg="green")
frame3.place(relx=0, rely=0, relwidth=1, relheight=.4)  

frame4 = tk.Frame(frame1, bg="green")
frame4.place(relx=0, rely=0, relwidth=1, relheight=.4)  

label = tk.Label(frame3, text="Hola Bienvenido \n a nuestra gestor", bg="red")
label.place(relx=0.5, rely=0.5, anchor="center")
root.mainloop()