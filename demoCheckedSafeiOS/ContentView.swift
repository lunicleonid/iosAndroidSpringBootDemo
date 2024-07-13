import SwiftUI

struct ContentView: View {
    
    @State private var fuelLevel: Bool = true
    @State private var warningLights: Bool = false
    @State private var wipers: Bool = false
    @State private var mirrors: Bool = false
    @State private var tyres: Bool = false
    @State private var lights: Bool = false
    @State private var brakes: Bool = false
    @State private var exhaustSmoke: Bool = false
    @State private var selectedOption: String = "No"
    private let options = ["Yes", "No"]
    
    var body: some View {
        GeometryReader { geometry in
            VStack(alignment: .leading) {
                
                HStack {
                    Text("FUEL LEVEL - SUFFICIENT TO DRIVE TO GARAGE/JOB")
                        .font(.system(size: 15, weight: .semibold, design: .default)).fixedSize(horizontal: false, vertical: true)
                    Spacer()
                    Menu {
                        ForEach(options, id: \.self) { option in
                            Button(action: {
                                selectedOption = option
                            }) {
                                Text(option)
                            }
                        }
                    } label: {
                        HStack {
                            Text(selectedOption)
                            Image(systemName: "chevron.down")
                        }
                        .padding()
                        .background(Color(.systemGray6))
                        .cornerRadius(8)
                        .overlay(
                            RoundedRectangle(cornerRadius: 8)
                                .stroke(Color.gray, lineWidth: 1)
                        )
                    }
                }
                Divider()
                
                InspectionItemView(title: "Dashboard any warning lights", isChecked: $warningLights)
                InspectionItemView(title: "Wipers / Washers", isChecked: $wipers)
                InspectionItemView(title: "Visual Check of Mirrors and Glass", isChecked: $mirrors)
                InspectionItemView(title: "Visual check of Tyres, condition, tread depth, security of wheel nuts", isChecked: $tyres)
                InspectionItemView(title: "Lights/indicators lenses clean and bulbs operating", isChecked: $lights)
                InspectionItemView(title: "Check Brakes ABS/EBS", isChecked: $brakes)
                InspectionItemView(title: "Check excessive exhaust Smoke", isChecked: $exhaustSmoke)
                
                HStack {
                    Button(action: {
                        // Previous action
                    }) {
                        Text("Previous")
                            .frame(minWidth: 100)
                            .padding()
                            .background(Color.gray.opacity(0.2))
                            .cornerRadius(8)
                    }
                    .buttonStyle(PlainButtonStyle())
                    
                    Spacer()
                    Button(action: {
                        print("LLL \(selectedOption)")
                        sendData()
                    }) {
                        Text("Next")
                            .frame(minWidth: 100)
                            .padding()
                            .background(Color.gray)
                            .foregroundColor(.white)
                            .cornerRadius(8)
                    }
                    .buttonStyle(PlainButtonStyle())
                }
            }
            .navigationTitle("Vehicle Inspection")
            .padding()
        }
        .frame(minWidth: 300)
    }
    
    func sendData() {
           // Define the data struct
           struct CheckData: Codable {
               var fuelLevel: Bool
               var warningLights: Bool
               var wipers: Bool
               var mirrors: Bool
               var tyres: Bool
               var lights: Bool
               var brakes: Bool
               var exhaustSmoke: Bool
               var selectedOption: String
           }
           
           // Create an instance of the data
           let checkData = CheckData(
               fuelLevel: fuelLevel,
               warningLights: warningLights,
               wipers: wipers,
               mirrors: mirrors,
               tyres: tyres,
               lights: lights,
               brakes: brakes,
               exhaustSmoke: exhaustSmoke,
               selectedOption: selectedOption
           )
           
           // Encode the data
           guard let url = URL(string: "http://localhost:8080/demo") else { return }
           var request = URLRequest(url: url)
           request.httpMethod = "POST"
           request.setValue("application/json", forHTTPHeaderField: "Content-Type")
           
           do {
               let jsonData = try JSONEncoder().encode(checkData)
               request.httpBody = jsonData
               
               // Send the request
               URLSession.shared.dataTask(with: request) { data, response, error in
                   if let error = error {
                       print("Error: \(error.localizedDescription)")
                       return
                   }
                   
                   if let response = response as? HTTPURLResponse {
                       print("Response status code: \(response.statusCode)")
                   }
                   
                   if let data = data {
                       print("Response data: \(String(data: data, encoding: .utf8) ?? "No data")")
                   }
               }.resume()
           } catch {
               print("Error encoding data: \(error.localizedDescription)")
           }
       }
}

struct InspectionItemView: View {
    var title: String
    @Binding var isChecked: Bool
    
    @State private var selectedOption: String? = nil
    
    func radioGroupCallback(id: String) {
        selectedOption = id
        isChecked = selectedOption == "Pass"
    }
    
    var body: some View {
        HStack {
            Text(title).font(.system(size: 15, weight: .semibold, design: .default)).fixedSize(horizontal: false, vertical: true)
            
            Spacer()
            HStack {
                RadioButtonField(id: "Pass", label: "Pass", isMarked: selectedOption == "Pass", callback: radioGroupCallback)
                RadioButtonField(id: "Fail", label: "Fail", isMarked: selectedOption == "Fail", callback: radioGroupCallback)
            }
        }
        
        Divider().padding(.bottom, 16)
    }
}

struct RadioButtonField: View {
    @Environment(\.colorScheme) var colorScheme
    
    let id: String
    let label: String
    let isMarked: Bool
    let callback: (String) -> ()
    
    var body: some View {
        Button(action:{
            self.callback(self.id)
        }) {
            HStack {
                Image(systemName: self.isMarked ? "largecircle.fill.circle" : "circle")
                    .foregroundColor(colorScheme == .dark ? .white : .black)
                Text(label)
                    .foregroundColor(colorScheme == .dark ? .white : .black)
            }
        }
        .foregroundColor(.black)
    }
}


struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
